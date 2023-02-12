package api.model;

//User data set
public class UniqUser {
    private String name;
    private String email;
    private String password;

    public UniqUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
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

}