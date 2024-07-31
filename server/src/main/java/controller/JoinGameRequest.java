package controller;

public record JoinGameRequest(String playerColor, int gameID) {
    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }
}
