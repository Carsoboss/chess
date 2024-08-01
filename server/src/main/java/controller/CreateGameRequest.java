package controller;

public record CreateGameRequest(String gameName) {
    public String getGameName() {
        return gameName;
    }
}