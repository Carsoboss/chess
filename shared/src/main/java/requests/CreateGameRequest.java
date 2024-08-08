package requests;

public record CreateGameRequest(String gameName) {
    public String getGameName() {
        return gameName;
    }
}