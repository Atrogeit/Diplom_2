import api.model.Ingredient;
import api.model.UniqUser;
import api.user.UserResponseSetUp;
import api.util.UserGenerator;
import api.util.UserData;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import api.user.UserIngredients;
import api.user.UserOrder;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetUserOrderTest {


    private UniqUser uniqUser;

    private UserResponseSetUp UserResponseSetUp;

    private UserIngredients userIngredients;
    private Ingredient ingredient;
    private List<String> ingredientList;

    private UserOrder userOrder;
    private String accessToken;
    private static final String NEED_AUTH_TEXT = "You should be authorised";

    @Before
    public void setUp() {
        uniqUser = UserGenerator.getUser();
        UserResponseSetUp = new UserResponseSetUp();
        userIngredients = new UserIngredients();
        userOrder = new UserOrder();
        ingredientList = new ArrayList<>();
    }

    @After
    public void cleanUp() {
        if ( accessToken != null) {
            UserResponseSetUp.delete(accessToken);
        }
    }

    @Test
    public void getOrderNonAuthUserReturnStatus401AndMessageAboutAuth() {
        ValidatableResponse response = userOrder.get("");
        int statusCode = response.extract().statusCode();

        assertEquals(SC_UNAUTHORIZED, statusCode);

        String message = response.extract().path("message");

        assertEquals(NEED_AUTH_TEXT, message);
    }

    @Test
    public void getOrderAuthUserReturnStatus200AndListOrder() {
        UserResponseSetUp.create(uniqUser);
        ValidatableResponse loginResponse = UserResponseSetUp.login(UserData.from(uniqUser));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse getIngredientResponse = userIngredients.get();
        statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]");
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = userOrder.create(ingredient, accessToken);
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);


        ValidatableResponse userOrderResponse = userOrder.get(accessToken);
        statusCode = userOrderResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        int total = userOrderResponse.extract().path("total");
        int totalToday = userOrderResponse.extract().path("totalToday");
        List<Object> orders = userOrderResponse.extract().path("orders");

        assertTrue(orders.size() > 0);
        assertEquals(1, total);
        assertEquals(1, totalToday);
    }
}
