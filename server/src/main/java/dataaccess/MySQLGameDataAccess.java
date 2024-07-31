package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import model.GameData;
import chess.ChessGame;

public class MySQLGameDataAccess implements GameDataAccess {

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO Games (gameName, gameState) VALUES (?, ?)";
        ChessGame game = new ChessGame();
        String gameState = serialize(game);  // Ensure you implement serialize() properly
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, gameName);
            stmt.setString(2, gameState);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int gameID = generatedKeys.getInt(1);
                    return new GameData(gameID, gameName, game);
                } else {
                    throw new DataAccessException("Failed to create game, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating game", e);
        }
    }

    @Override
    public Collection<GameData> listAllGames() throws DataAccessException {
        String sql = "SELECT * FROM Games";
        Collection<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ChessGame game = deserialize(rs.getString("gameState"));
                games.add(new GameData(rs.getInt("gameID"), rs.getString("gameName"), game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while listing all games", e);
        }
        return games;
    }

    @Override
    public void joinGame(ChessGame.TeamColor color, int gameID, String username) throws DataAccessException {
        String sql = "UPDATE Games SET " + (color == ChessGame.TeamColor.WHITE ? "whiteUsername" : "blackUsername") + " = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while joining game", e);
        }
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting all games", e);
        }
    }

    // Placeholder for serialize method
    private String serialize(ChessGame game) {
        // Implement the actual serialization logic
        return "";
    }

    // Placeholder for deserialize method
    private ChessGame deserialize(String gameState) {
        // Implement the actual deserialization logic
        return new ChessGame();
    }
}
