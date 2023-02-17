import api.model.UniqUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import api.user.UserResponseSetUp;
import api.util.UserData;
import api.util.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;


public class UserCreationTest {
    private UniqUser uniqUser;
    private UserResponseSetUp UserResponseSetUp;
    private String token;
    private static final String USER_EXISTS_TEXT = "User already exists";
    private static final String MESSAGE_NOT_ENOUGH_DATA = "Email, password and name are required fields";


    @Before
    public void setUp() {
        uniqUser = UserGenerator.getUser();
        UserResponseSetUp = new UserResponseSetUp();
    }
    @Test
    //Check creation of unique user
    @DisplayName("Check creation of unique user")
    public void checkUserCreation() {
        ValidatableResponse response = UserResponseSetUp.create(uniqUser);
        int statusCode = response.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserResponseSetUp.login(UserData.from(uniqUser));
        statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        token = loginResponse.extract().path("accessToken");
    }

    @Test
    //Check creation of already existing user
    @DisplayName("Check creation of already existing user")
    public void checkCreationUserAlreadyExists(){
        UserResponseSetUp.create(uniqUser);
        ValidatableResponse response = UserResponseSetUp.create(uniqUser);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);

        String message = response.extract().path("message");
        assertEquals(message, USER_EXISTS_TEXT);
    }

    @Test
    //Check user creation without email
    @DisplayName("Check user creation without email")
    public void checkUserCreationWithoutEmail(){
        UniqUser userWithoutEmail = UserGenerator.getUserWithoutEmail();
        ValidatableResponse response = UserResponseSetUp.create(userWithoutEmail);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);

        String message = response.extract().path("message");
        assertEquals(message, MESSAGE_NOT_ENOUGH_DATA);
    }
    @After
    public void cleanUp() {
        if ( token != null) {
            UserResponseSetUp.delete(token);
        }
    }
}