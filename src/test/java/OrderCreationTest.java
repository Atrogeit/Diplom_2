import api.model.Ingredient;
import api.model.UniqUser;
import api.util.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import api.user.UserIngredients;
import api.user.UserOrder;
import api.user.UserResponseSetUp;
import api.util.UserData;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;


public class OrderCreationTest {
    private UniqUser uniqUser;
    private UserResponseSetUp UserResponseSetUp;
    private UserIngredients userIngredients;
    private Ingredient ingredient;
    private List<String> ingredientList;
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
    @Test
    @DisplayName("Check order creation returns 200OK and SuccessTrue for authorized user")
    public void checkOrderCreationReturns200OKAndSuccessTrue() {
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
    @DisplayName("Check order creation with unauthorized user returns 400")
    //Тут есть баг, так как согласно документации создать заказ может только авторизованный пользователь
    //Но при создании заказа неавторизованным пользователем возвращается 200ОК
    public void checkOrderCreationWithUnauthorizedUserReturns400() {
        ValidatableResponse getIngredientResponse = userIngredients.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]");
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = userOrder.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        Boolean statusOrder = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int numberOrder = orderResponse.extract().path("order.number");

        assertTrue(statusOrder);
        assertFalse(burgerName.isEmpty());
        assertTrue(numberOrder > 0);
    }

    @Test
    @DisplayName("Check order creation without ingredients returns 400 and mistake message")
    public void checkOrderCreationWithoutIngredientsReturns400AndMistakeMessage() {
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
    @DisplayName("Check order creation with invalid ingredient hash returns 500")
    public void checkOrderCreationWithInvalidIngredientHashReturns500() {
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
    @After
    public void cleanUp() {
        if ( accessToken != null) {
            UserResponseSetUp.delete(accessToken);
        }
    }
}

