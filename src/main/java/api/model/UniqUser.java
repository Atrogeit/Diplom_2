package api.model;

//User data set
public class UniqUser {
    private String email;
    private String password;
    private String name;

    public UniqUser(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;

    }

    public UniqUser(String email, String name) {
        this.email = email;
        this.name = name;

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}