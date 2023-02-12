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
import static org.junit.Assert.*;


public class UserLoginTest {
    private UniqUser uniqUser;
    private UserResponseSetUp UserResponseSetUp;
    private String accessToken;
    private static final String MESSAGE_INCORRECT_AUTH_DATA = "email or password are incorrect";

    @Before
    public void setUp() {
        uniqUser = UserGenerator.getUser();
        UserResponseSetUp = new UserResponseSetUp();

    }
    @Test
    @DisplayName("Check for successful user authorization returns 200OK and token")
    public void checkSuccessfulLoginReturns200okAndToken() {
        UserResponseSetUp.create(uniqUser);
        ValidatableResponse loginResponse = UserResponseSetUp.login(UserData.from(uniqUser));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);


        accessToken = loginResponse.extract().path("accessToken");
        String refreshToken = loginResponse.extract().path("refreshToken");
        Boolean success = loginResponse.extract().path("success");


        assertFalse(accessToken.isEmpty());
        assertFalse(refreshToken.isEmpty());
        assertTrue(success);
    }

    @Test
    @DisplayName("Check for unable to authorized without login returns 401 and error message")
    public void checkAuthorizationWithoutLoginReturns401AndErrorMessage(){
        UserData userWithoutLogin = new UserData("", uniqUser.getPassword());
        ValidatableResponse loginResponse = UserResponseSetUp.login(userWithoutLogin);
        int statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_UNAUTHORIZED, statusCode);

        String message = loginResponse.extract().path("message");

        assertEquals(MESSAGE_INCORRECT_AUTH_DATA, message);
    }
    @After
    public void cleanUp() {
        if ( accessToken != null) {
            UserResponseSetUp.delete(accessToken);
        }
    }

}