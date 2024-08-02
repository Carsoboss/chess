package dataaccess;

import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDataAccess implements GameDataAccess {

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if (isGameNameTaken(gameName)) {
            throw new DataAccessException("Error: Game name already taken");
        }
        String sql = "INSERT INTO Games (game_name) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, gameName);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameId = rs.getInt(1);
                    return new GameData(gameId, null, null, gameName, null);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting games: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listAllGames() throws DataAccessException {
        String sql = "SELECT * FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Collection<GameData> games = new ArrayList<>();
            while (rs.next()) {
                int gameId = rs.getInt("id");
                String gameName = rs.getString("game_name");
                String whitePlayer = rs.getString("white_player");
                String blackPlayer = rs.getString("black_player");
                String gameState = rs.getString("game_state");
                games.add(new GameData(gameId, whitePlayer, blackPlayer, gameName, gameState));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
    }

    @Override
    public void joinGame(String playerColor, int gameID, String username) throws DataAccessException {
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new DataAccessException("Error: Invalid player color");
        }

        GameData game = getGame(gameID);
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
        }

        String sql = "UPDATE Games SET white_player = ?, black_player = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setInt(3, gameID);
            stmt.executeUpdate();
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
                    String gameName = rs.getString("game_name");
                    String whitePlayer = rs.getString("white_player");
                    String blackPlayer = rs.getString("black_player");
                    String gameState = rs.getString("game_state");
                    return new GameData(gameID, whitePlayer, blackPlayer, gameName, gameState);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
        return null;
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
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking game name: " + e.getMessage());
        }
        return false;
    }
}
