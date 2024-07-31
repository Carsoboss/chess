package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements IGameDAO {
    private int nextId = 1;
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData createGame(String gameName) {
        int gameId = nextId++;
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameId, null, null, gameName, game);
        games.put(gameId, gameData);
        return gameData;
    }

    @Override
    public void deleteAllGames() throws DAOException {

    }

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void addPlayerToGame(ChessGame.TeamColor color, int gameId, String username) throws DAOException {
        GameData game = games.get(gameId);
        if (game == null) {
            throw new DAOException("Game not found");
        }

        String currentPlayer = (color == ChessGame.TeamColor.WHITE) ? game.whiteUsername() : game.blackUsername();
        if (currentPlayer != null && !currentPlayer.isEmpty()) {
            throw new DAOException("Position already taken");
        }

        GameData updatedGame = (color == ChessGame.TeamColor.WHITE)
                ? new GameData(gameId, username, game.blackUsername(), game.gameName(), game.game())
                : new GameData(gameId, game.whiteUsername(), username, game.gameName(), game.game());
        games.put(gameId, updatedGame);
    }
}
