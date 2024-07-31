package service;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.InMemoryAuthDataAccess;
import dataaccess.InMemoryUserDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDataAccess userDataAccess = new InMemoryUserDataAccess();
    private final AuthDataAccess authDataAccess = new InMemoryAuthDataAccess();

    public AuthData registerUser(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new DataAccessException("Error: Bad request");
        }
        userDataAccess.addUser(userData);
        return authDataAccess.createAuth(userData.username());
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        UserData existingUser = userDataAccess.getUser(userData.username());
        if (existingUser == null || !existingUser.password().equals(userData.password())) {
            throw new DataAccessException("Error: Unauthorized");
        }
        return authDataAccess.createAuth(userData.username());
    }

    public void logoutUser(String authToken) throws DataAccessException {
        if (authDataAccess.retrieveAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        authDataAccess.deleteAuth(authToken);
    }
}
