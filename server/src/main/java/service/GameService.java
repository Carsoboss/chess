package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GameData createGame(String gameName) {
        return gameDAO.createGame(gameName);
    }

    public List<GameData> listGames() {
        return List.copyOf(gameDAO.games.values());
    }

    public GameData joinGame(int gameID, String playerColor, String username) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Color already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Color already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Invalid color");
        }
        gameDAO.games.put(gameID, game);
        return game;
    }

    public void clear() {
        gameDAO.clear();
    }
}
