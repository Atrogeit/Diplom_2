package api.util;
import api.model.UniqUser;

public class UserData {
    private String email;
    private String password;
    private String name;


    public UserData(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public static UserData from(UniqUser uniqUser) {
        return new UserData(uniqUser.getEmail(), uniqUser.getPassword());
    }
    //Созданные Idea Сеттеры и Геттеры
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
