package api.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserIngredients extends User {

    private static final String GET_PATH = "/api/ingredients";

    //Get list of ingredients step
    @Step("Get list of ingredients")
    public ValidatableResponse get() {
        return given()
                .spec(getSpec())
                .when()
                .get(GET_PATH)
                .then();
    }
}
