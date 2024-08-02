package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDataAccess implements AuthDataAccess {

    @Override
    public AuthData createAuth(String username, String authToken) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (username, token, expiration) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 1 DAY))";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, authToken);
            stmt.executeUpdate();
            return new AuthData(username, authToken);
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting auth token: " + e.getMessage());
        }
    }

    @Override
    public AuthData retrieveAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM AuthTokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString(1);
                    return new AuthData(username, authToken);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllAuths() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting all auth tokens: " + e.getMessage());
        }
    }
}
