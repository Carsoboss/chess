package dataaccess;

import model.UserData;
import db.DBManager;
import exception.DataException;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public void addUser(UserData user) throws DataException {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        DBManager.executeUpdate(query, user.username(), user.password(), user.email());
    }

    @Override
    public UserData findUserByUsername(String username) throws DataException {
        String query = "SELECT * FROM users WHERE username=?";
        return DBManager.executeQuery(query, rs -> {
            if (rs.next()) {
                String password = rs.getString("password");
                String email = rs.getString("email");
                return new UserData(username, password, email);
            }
            return null;
        }, username);
    }

    @Override
    public void deleteAllUsers() throws DataException {
        String query = "DELETE FROM users";
        DBManager.executeUpdate(query);
    }

    @Override
    public int countUsers() throws DataException {
        String query = "SELECT COUNT(username) AS userCount FROM users";
        return DBManager.executeQuery(query, rs -> rs.next() ? rs.getInt("userCount") : 0);
    }
}
