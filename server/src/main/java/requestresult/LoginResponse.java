package requestresult;

public class LoginResponse {
    private final String username;
    private final String authToken;

    public LoginResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
