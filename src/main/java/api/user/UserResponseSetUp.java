package api.user;
import api.model.UniqUser;
import api.util.UserData;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserResponseSetUp extends User {

    //Creating constants
    private static final String CREATE_PATH = "/api/auth/register";

    private static final String LOGIN_PATH = "/api/auth/login";

    private static final String DELETE_PATH = "/api/auth/user";

    private static final String UPDATE_PATH = "/api/auth/user";

    //Step to create User
    @Step("Создание пользователя")
    public ValidatableResponse create (UniqUser uniqUser) {
        return given()
                .spec(getSpec())
                .body(uniqUser)
                .when()
                .post(CREATE_PATH)
                .then();
    }

    //User authorization step
    @Step("Авторизация юзера")
    public ValidatableResponse login(UserData userData) {
        return given()
                .spec(getSpec())
                .body(userData)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    //User data refresh
    @Step("Обновление данных юзера")
    public ValidatableResponse update(UniqUser uniqUser, String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(uniqUser)
                .when()
                .patch(UPDATE_PATH)
                .then();
    }

    //Deleting user step
    @Step("Удаление юзера")
    public ValidatableResponse delete(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .delete(DELETE_PATH)
                .then();
    }
}
