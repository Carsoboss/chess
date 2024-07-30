package requestresult;

public class ListGamesRequest {
    private final String authToken;

    public ListGamesRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
