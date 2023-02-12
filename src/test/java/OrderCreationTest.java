import api.model.Ingredient;
//import api.model.User;
import api.model.UniqUser;
import api.util.UserGenerator;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import api.client.IngredientClient;
import api.user.UserIngredients;
//import api.client.OrderClient;
import api.user.UserOrder;
//import api.client.UserClient;
import api.user.UserResponseSetUp;
//import api.util.UserCredentials;
import api.util.UserData;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;


public class OrderCreationTest {

    //private User user;
    private UniqUser uniqUser;
    //private UserClient UserClient;
    private UserResponseSetUp UserResponseSetUp;
    //private IngredientClient ingredientClient;
    private UserIngredients userIngredients;
    private Ingredient ingredient;
    private List<String> ingredientList;
    //private OrderClient orderClient;
    private UserOrder userOrder;
    private String accessToken;
    private static final String NEED_INGREDIENT_IDS_TEXT = "Ingredient ids must be provided";

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
    public void createOrderWithAuthReturnsSuccessTrueAndStatus200() {
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

        Boolean statusOrder = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int numberOrder = orderResponse.extract().path("order.number");

        assertTrue(statusOrder);
        assertFalse(burgerName.isEmpty());
        assertTrue(numberOrder > 0);
    }

    @Test
    public void createOrderWithoutAuthReturnsSuccessTrueAndStatus200() {
        ValidatableResponse getIngredientResponse = userIngredients.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]");
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = userOrder.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        Boolean statusOrder = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int numberOrder = orderResponse.extract().path("order.number");

        assertTrue(statusOrder);
        assertFalse(burgerName.isEmpty());
        assertTrue(numberOrder > 0);
    }

    @Test
    public void createOrderWithoutIngredientsReturnsStatus400AndMessageWithMistake() {
        ValidatableResponse getIngredientResponse = userIngredients.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = userOrder.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        Boolean statusOrder = orderResponse.extract().path("success");
        String message = orderResponse.extract().path("message");

        assertFalse(statusOrder);
        assertEquals(NEED_INGREDIENT_IDS_TEXT, message);
    }

    @Test
    public void createOrderWithInvalidHashOfIngredientReturnsStatus500() {
        ValidatableResponse getIngredientResponse = userIngredients.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]") + "1";
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = userOrder.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();

        assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}

