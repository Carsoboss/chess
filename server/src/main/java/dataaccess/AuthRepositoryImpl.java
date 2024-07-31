package dataaccess;

import model.AuthData;
import db.DBManager;
import exception.DataException;

import java.util.UUID;

public class AuthRepositoryImpl implements AuthRepository {

    @Override
    public AuthData generateAuthToken(String username) throws DataException {
        if (username == null) {
            throw new DataException("Invalid username");
        }

        String authToken = UUID.randomUUID().toString();
        int userId = getUserIdByUsername(username);

        String query = "INSERT INTO auths (userid, authtoken) VALUES (?, ?)";
        DBManager.executeUpdate(query, userId, authToken);

        return new AuthData(authToken, username);
    }

    @Override
    public AuthData findAuthByToken(String authToken) throws DataException {
        String query = "SELECT userid, authtoken FROM auths WHERE authtoken=?";
        return DBManager.executeQuery(query, rs -> {
            if (rs.next()) {
                int userId = rs.getInt("userid");
                String username = getUsernameByUserId(userId);
                return new AuthData(authToken, username);
            }
            return null;
        }, authToken);
    }

    @Override
    public AuthData findAuthByUsername(String username) throws DataException {
        int userId = getUserIdByUsername(username);
        String query = "SELECT userid, authtoken FROM auths WHERE userid=?";
        return DBManager.executeQuery(query, rs -> {
            if (rs.next()) {
                String authToken = rs.getString("authtoken");
                return new AuthData(authToken, username);
            }
            return null;
        }, userId);
    }

    @Override
    public void removeAuth(String authToken) throws DataException {
        String query = "DELETE FROM auths WHERE authtoken=?";
        DBManager.executeUpdate(query, authToken);
    }

    @Override
    public void clearAllAuths() throws DataException {
        String query = "DELETE FROM auths";
        DBManager.executeUpdate(query);
    }

    @Override
    public int countAuths() throws DataException {
        String query = "SELECT COUNT(userid) AS authCount FROM auths";
        return DBManager.executeQuery(query, rs -> rs.next() ? rs.getInt("authCount") : 0);
    }

    private int getUserIdByUsername(String username) throws DataException {
        String query = "SELECT userid FROM users WHERE username=?";
        return DBManager.executeQuery(query, rs -> rs.next() ? rs.getInt("userid") : -1, username);
    }

    private String getUsernameByUserId(int userId) throws DataException {
        String query = "SELECT username FROM users WHERE userid=?";
        return DBManager.executeQuery(query, rs -> rs.next() ? rs.getString("username") : null, userId);
    }
}
