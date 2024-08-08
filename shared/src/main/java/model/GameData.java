package model;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, String gameState) {
    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameState() {
        return gameState;
    }
}
