package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryGameDataAccess implements GameDataAccess {
    private int nextId = 1;
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if (isGameNameTaken(gameName)) {
            throw new DataAccessException("Error: Game name already taken");
        }
        int gameId = nextId++;
        GameData newGame = new GameData(gameId, null, null, gameName, null);
        games.put(gameId, newGame);
        return newGame;
    }

    @Override
    public void deleteAllGames() {
        games.clear();
    }

    @Override
    public Collection<GameData> listAllGames() {
        return games.values();
    }

    @Override
    public void joinGame(String playerColor, int gameID, String username) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }
        if ("WHITE".equals(playerColor)) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: White player already assigned");
            }
            game = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        } else if ("BLACK".equals(playerColor)) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: Black player already assigned");
            }
            game = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: Invalid player color");
        }
        games.put(gameID, game);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public boolean isGameNameTaken(String gameName) {
        return games.values().stream()
                .anyMatch(game -> game.gameName().equalsIgnoreCase(gameName));
    }
}