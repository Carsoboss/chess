package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        userDAO.insertUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData storedUser = userDAO.getUser(user.username());
        if (!storedUser.password().equals(user.password())) {
            throw new DataAccessException("Unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
