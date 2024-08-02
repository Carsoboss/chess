package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public AuthData registerUser(UserData userData) throws DataAccessException {
        // Validate that all required fields are present
        if (userData.username() == null || userData.username().isEmpty() ||
                userData.password() == null || userData.password().isEmpty() ||
                userData.email() == null || userData.email().isEmpty()) {
            throw new DataAccessException("Error: bad request - missing fields");
        }

        // Check if the user already exists
        if (userDataAccess.getUser(userData.username()) != null) {
            throw new DataAccessException("Error: Username already taken");
        }

        // Hash the password before storing it
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        // Create a new UserData object with the hashed password
        UserData hashedUserData = new UserData(userData.username(), hashedPassword, userData.email());

        // Add the new user and create an authentication token
        userDataAccess.addUser(hashedUserData);
        return authDataAccess.createAuth(userData.username());
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        UserData existingUser = userDataAccess.getUser(userData.username());
        if (existingUser == null || !BCrypt.checkpw(userData.password(), existingUser.password())) {
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
