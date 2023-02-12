package api.util;
import api.model.UniqUser;
import com.github.javafaker.Faker;

public class UserGenerator {
    public static Faker faker = new Faker();

    private static String email = faker.internet().emailAddress();
    private static String password = faker.internet().password();
    private static String name = faker.name().name();

    public static UniqUser getUser() {
        return new UniqUser(email, password, name);
    }

    public static UniqUser getCourierWithoutEmail() {
        return new UniqUser("", password, name);
    }
}
