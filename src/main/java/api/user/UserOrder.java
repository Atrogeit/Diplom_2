package api.user;
import api.model.Ingredient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserOrder extends User {
    private static final String PATH = "/api/orders";

    //Create an order step
    @Step("Order creation")
    public ValidatableResponse create(Ingredient ingredient, String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(ingredient)
                .when()
                .post(PATH)
                .then();
    }

    //Get list of orders step
    @Step("Get list of orders")
    public ValidatableResponse get(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .get(PATH)
                .then();
    }
}
