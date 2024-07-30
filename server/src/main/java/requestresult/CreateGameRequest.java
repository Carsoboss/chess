package requestresult;

public class CreateGameRequest {
    private final String authToken;
    private final String gameName;

    public CreateGameRequest(String authToken, String gameName) {
        this.authToken = authToken;
        this.gameName = gameName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getGameName() {
        return gameName;
    }
}
