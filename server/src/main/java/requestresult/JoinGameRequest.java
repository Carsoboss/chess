package requestresult;

import chess.ChessGame;

public class JoinGameRequest {
    private final String authToken;
    private final ChessGame.TeamColor playerColor;
    private final int gameId;

    public JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, int gameId) {
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.gameId = gameId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public int getGameId() {
        return gameId;
    }
}
