package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.AuthData;

public class MySQLAuthDataAccess implements AuthDataAccess {

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String sql = "INSERT INTO Auths (username, authToken) VALUES (?, ?)";
        String authToken = generateAuthToken();  // You need to implement this method
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating auth data", e);
        }
        return new AuthData(authToken, username);
    }

    private String generateAuthToken() {
    }

    @Override
    public AuthData retrieveAuthByAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM Auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while retrieving auth by token", e);
        }
    }

    @Override
    public AuthData retrieveAuthByUsername(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM Auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting auth", e);
        }
    }

    @Override
    public void deleteAllAuths() throws DataAccessException {
        String sql = "DELETE FROM Auths";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting all auths", e);
        }
    }

    @Override
    public int retrieveNumAuths() throws DataAccessException {
        return 0;
    }
}
