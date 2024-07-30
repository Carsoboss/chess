package requestresult;

public class CreateGameResponse {
    private final int gameId;

    public CreateGameResponse(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }
}
