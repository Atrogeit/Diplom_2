//import api.model.User;
import api.model.UniqUser;
import io.restassured.response.ValidatableResponse;
//import api.client.UserClient;
import api.user.UserResponseSetUp;
//import api.util.UserCredentials;
import api.util.UserData;
import api.util.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;


public class UserCreationTest {

    //private User user;
    private UniqUser uniqUser;
    //private UserClient UserClient;
    private UserResponseSetUp UserResponseSetUp;
    private String token;
    private static final String USER_EXISTS_TEXT = "User already exists";
    private static final String MESSAGE_NOT_ENOUGH_DATA = "Email, password and name are required fields";


    @Before
    public void setUp() {
        uniqUser = UserGenerator.getUser();
        UserResponseSetUp = new UserResponseSetUp();
    }

    @After
    public void cleanUp() {
        if ( token != null) {
            UserResponseSetUp.delete(token);
        }
    }

    @Test
    //Можно создать юзера
    public void CreateNewUser() {
        ValidatableResponse response = UserResponseSetUp.create(uniqUser);
        int statusCode = response.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserResponseSetUp.login(UserData.from(uniqUser));
        statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        token = loginResponse.extract().path("accessToken");
    }

    @Test
    //Невозможно создать уже существующего юзера
    public void MustNotCreateNewUserWithExistingData(){
        UserResponseSetUp.create(uniqUser);
        ValidatableResponse response = UserResponseSetUp.create(uniqUser);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);

        String message = response.extract().path("message");
        assertEquals(message, USER_EXISTS_TEXT);
    }

    @Test
    //Невозможно создать юзера без почты
    public void CreateNewCourierWithoutLogin(){
        UniqUser courierWithoutEmail = UserGenerator.getCourierWithoutEmail();
        ValidatableResponse response = UserResponseSetUp.create(courierWithoutEmail);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);


        String message = response.extract().path("message");
        assertEquals(message, MESSAGE_NOT_ENOUGH_DATA);

    }
}