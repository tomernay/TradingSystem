package Presentaion.application;

public class SubscriberDetails {
    private String username;
    private String token;

    // Constructors, getters, and setters

    public SubscriberDetails(String username, String token) {
        this.username = username;
        this.token = token;
    }

    // Getters and setters for username and token
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
