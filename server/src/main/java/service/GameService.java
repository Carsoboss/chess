package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.List;

public class GameService {

    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;

    public GameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        validateAuth(authToken);
        return List.copyOf(gameDataAccess.listAllGames());
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        validateAuth(authToken);
        return gameDataAccess.createGame(gameName);
    }

    public GameData joinGame(String playerColor, int gameID, String authToken) throws DataAccessException {
        validateAuth(authToken);
        String username = authDataAccess.retrieveAuth(authToken).username();
        gameDataAccess.joinGame(playerColor, gameID, username);
        return gameDataAccess.getGame(gameID);
    }

    private void validateAuth(String authToken) throws DataAccessException {
        if (authDataAccess.retrieveAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
    }
}

