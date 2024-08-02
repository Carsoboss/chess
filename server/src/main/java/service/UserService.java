package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public AuthData registerUser(UserData userData) throws DataAccessException {
        validateUserData(userData);

        // Check if the username is already taken
        if (userDataAccess.getUser(userData.username()) != null) {
            throw new DataAccessException("Error: Username already taken");
        }

        // Hash the password and create a new user
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        UserData newUser = new UserData(userData.username(), hashedPassword, userData.email());

        // Store the new user and create an authentication token
        userDataAccess.addUser(newUser);
        return generateAuthToken(newUser.username());
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        validateLoginData(userData);

        // Retrieve the stored user data
        UserData storedUser = userDataAccess.getUser(userData.username());
        if (storedUser == null || !BCrypt.checkpw(userData.password(), storedUser.password())) {
            throw new DataAccessException("Error: Invalid username or password");
        }

        // Generate a new authentication token
        return generateAuthToken(userData.username());
    }

    public void logoutUser(String authToken) throws DataAccessException {
        if (authDataAccess.retrieveAuth(authToken) == null) {
            throw new DataAccessException("Error: Invalid authentication token");
        }
        authDataAccess.deleteAuth(authToken);
    }

    private void validateUserData(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.username().isEmpty() ||
                userData.password() == null || userData.password().isEmpty() ||
                userData.email() == null || userData.email().isEmpty()) {
            throw new DataAccessException("Error: Missing required fields");
        }
    }

    private void validateLoginData(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.username().isEmpty() ||
                userData.password() == null || userData.password().isEmpty()) {
            throw new DataAccessException("Error: Username or password cannot be empty");
        }
    }

    private AuthData generateAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        return authDataAccess.createAuth(username, authToken);
    }
}
