package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDataAccess {
    GameData createGame(String gameName) throws DataAccessException;
    void deleteAllGames() throws DataAccessException;
    Collection<GameData> listAllGames() throws DataAccessException;
    void joinGame(String playerColor, int gameID, String username) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    boolean isGameNameTaken(String gameName) throws DataAccessException;
}