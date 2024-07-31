package dataaccess;

import model.GameData;
import chess.ChessGame;

import java.util.Collection;

public interface GameDataAccess {
    GameData createGame(String gameName) throws DataAccessException;
    void joinGame(ChessGame.TeamColor color, int gameID, String username) throws DataAccessException;
    void deleteAllGames() throws DataAccessException;
    Collection<GameData> listAllGames() throws DataAccessException;
}
