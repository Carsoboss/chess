package dataaccess;

import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDataAccess implements GameDataAccess {

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO Games (game_name) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, gameName);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating game failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int gameId = generatedKeys.getInt(1);
                    return new GameData(gameId, null, null, gameName, null);
                } else {
                    throw new DataAccessException("Creating game failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting all games: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listAllGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GameData game = new GameData(
                        rs.getInt("id"),
                        rs.getString("white_player"),
                        rs.getString("black_player"),
                        rs.getString("game_name"),
                        rs.getString("game_state")
                );
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing all games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void joinGame(String playerColor, int gameID, String username) throws DataAccessException {
        String sql;
        if ("WHITE".equalsIgnoreCase(playerColor)) {
            sql = "UPDATE Games SET white_player = ? WHERE id = ?";
        } else if ("BLACK".equalsIgnoreCase(playerColor)) {
            sql = "UPDATE Games SET black_player = ? WHERE id = ?";
        } else {
            throw new DataAccessException("Invalid player color: " + playerColor);
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Joining game failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error joining game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("id"),
                            rs.getString("white_player"),
                            rs.getString("black_player"),
                            rs.getString("game_name"),
                            rs.getString("game_state")
                    );
                } else {
                    throw new DataAccessException("Game with ID " + gameID + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }

    @Override
    public boolean isGameNameTaken(String gameName) throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM Games WHERE game_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gameName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                } else {
                    throw new DataAccessException("Error checking if game name is taken.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if game name is taken: " + e.getMessage());
        }
    }
}
