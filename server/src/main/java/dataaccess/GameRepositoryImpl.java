package dataaccess;

import model.GameData;
import chess.ChessGame;
import db.DBManager;
import exception.DataException;

import com.google.gson.Gson;

import java.util.List;
import java.util.ArrayList;

public class GameRepositoryImpl implements GameRepository {

    @Override
    public GameData createGame(String gameName) throws DataException {
        ChessGame game = new ChessGame();
        String gameJson = new Gson().toJson(game);

        String query = "INSERT INTO games (gamename, gamejson) VALUES (?, ?)";
        int gameId = DBManager.executeUpdate(query, gameName, gameJson);

        return new GameData(gameId, null, null, gameName, game);
    }

    @Override
    public void deleteAllGames() throws DataException {
        String query = "DELETE FROM games";
        DBManager.executeUpdate(query);
    }

    @Override
    public List<GameData> findAllGames() throws DataException {
        String query = "SELECT * FROM games";
        return DBManager.executeQuery(query, rs -> {
            var games = new ArrayList<GameData>();
            while (rs.next()) {
                int gameId = rs.getInt("gameid");
                String whiteUsername = rs.getString("whiteusername");
                String blackUsername = rs.getString("blackusername");
                String gameName = rs.getString("gamename");
                String gameJson = rs.getString("gamejson");
                ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                games.add(new GameData(gameId, whiteUsername, blackUsername, gameName, game));
            }
            return games;
        });
    }

    @Override
    public void addPlayerToGame(ChessGame.TeamColor color, int gameId, String username) throws DataException {
        String query = "UPDATE games SET " +
                (color == ChessGame.TeamColor.WHITE ? "whiteusername=?" : "blackusername=?") +
                " WHERE gameid=?";
        DBManager.executeUpdate(query, username, gameId);
    }
}
