package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    // Corrected getters
    public int gameID() {
        return gameID;
    }

    public String whiteUsername() {
        return whiteUsername;
    }

    public String blackUsername() {
        return blackUsername;
    }

    public String gameName() {
        return gameName;
    }
}
