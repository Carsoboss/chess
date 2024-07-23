package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class GameDAO {
    public Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public GameData createGame(String gameName) {
        GameData game = new GameData(nextGameID++, null, null, gameName, null);
        games.put(game.gameID(), game);
        return game;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found");
        }
        return games.get(gameID);
    }

    public void clear() {
        games.clear();
    }
}
