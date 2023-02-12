
import api.model.UniqUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import api.user.UserResponseSetUp;
import api.util.UserData;
import api.util.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class ChangeUserDataTest {

    private UniqUser uniqUser;
    private UserResponseSetUp UserResponseSetUp;
    private String accessToken;

    //Constants
    private static final String email = "test-data@uandex.ru";
    private static final String name = "Praktikum";
    private static final String USER_SHOULD_BE_AUTHORISED_TEXT = "You should be authorised";


    @Before
    //Setting up the unique user generation
    public void setUp() {
        uniqUser = UserGenerator.getUser();
        UserResponseSetUp = new UserResponseSetUp();
    }

    @Test
    //Checking that authorized user might update user data
    @DisplayName("Checking that authorized user might update user data")
    public void checkUpdateDateForAuthorizedUser() {
        ValidatableResponse createResponse = UserResponseSetUp.create(uniqUser);
        int statusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserResponseSetUp.login(UserData.from(uniqUser));
        statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        accessToken = loginResponse.extract().path("accessToken");

        UniqUser newUserData = new UniqUser(email, name);
        ValidatableResponse updateResponse = UserResponseSetUp.update(newUserData, accessToken);
        statusCode = updateResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        String updatedEmail = updateResponse.extract().path("user.email");
        String updatedName = updateResponse.extract().path("user.name");

        assertEquals(email, updatedEmail);
        assertEquals(name, updatedName);
    }

    @Test
    //Unauthorized user can't update user data
    @DisplayName("Unauthorized user can't update user data")
    public void checkUnableUpdateDataWithoutUserAuthorization() {
        ValidatableResponse createResponse = UserResponseSetUp.create(uniqUser);
        int statusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserResponseSetUp.login(UserData.from(uniqUser));
        statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);
        accessToken = loginResponse.extract().path("accessToken");


        UniqUser newUserData = new UniqUser(email, name);

        ValidatableResponse updateResponse = UserResponseSetUp.update(newUserData, "");
        statusCode = updateResponse.extract().statusCode();

        assertEquals(SC_UNAUTHORIZED, statusCode);

        String message = updateResponse.extract().path("message");

        assertEquals(USER_SHOULD_BE_AUTHORISED_TEXT, message);
    }

    @After
    //Deleting user authorization token
    public void cleanUp() {
        if ( accessToken != null) {
            UserResponseSetUp.delete(accessToken);
        }
    }
}
