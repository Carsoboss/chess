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
        if (userData.username() == null || userData.username().isEmpty() ||
                userData.password() == null || userData.password().isEmpty() ||
                userData.email() == null || userData.email().isEmpty()) {
            throw new DataAccessException("Error: bad request - missing fields");
        }

        // Check if the user already exists
        if (userDataAccess.getUser(userData.username()) != null) {
            throw new DataAccessException("Error: Username already taken");
        }

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        UserData userWithHashedPassword = new UserData(userData.username(), hashedPassword, userData.email());

        // Add the new user and create an authentication token
        userDataAccess.addUser(userWithHashedPassword);
        String authToken = UUID.randomUUID().toString();
        return authDataAccess.createAuth(userWithHashedPassword.username(), authToken);
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.password() == null) {
            throw new DataAccessException("Error: Fields are blank");
        }

        UserData existingUser = userDataAccess.getUser(userData.username());
        if (existingUser == null || !BCrypt.checkpw(userData.password(), existingUser.password())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        return authDataAccess.createAuth(existingUser.username(), authToken);
    }

    public void logoutUser(String authToken) throws DataAccessException {
        if (authToken == null || authDataAccess.retrieveAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        authDataAccess.deleteAuth(authToken);
    }
}
