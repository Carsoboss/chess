package service;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.StorageException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requestresult.*;

public class UserService {

    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public RegisterUserResponse register(RegisterUserRequest request) throws StorageException {
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        UserData user = new UserData(request.getUsername(), hashedPassword, request.getEmail());
        userDataAccess.addUser(user);
        AuthData auth = authDataAccess.createAuth(user.username());
        return new RegisterUserResponse(user.username(), auth.authToken());
    }

    public LoginResponse login(LoginRequest request) throws StorageException {
        UserData user = userDataAccess.getUser(request.getUsername());
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.password())) {
            throw new StorageException("Unauthorized");
        }
        AuthData auth = authDataAccess.createAuth(user.username());
        return new LoginResponse(user.username(), auth.authToken());
    }

    public LogoutResponse logout(LogoutRequest request) throws StorageException {
        AuthData auth = authDataAccess.retrieveAuthByAuthToken(request.getAuthToken());
        if (auth == null) {
            throw new StorageException("Unauthorized");
        }
        authDataAccess.deleteAuth(auth.authToken());
        return new LogoutResponse();
    }

    public int getNumUsers() throws StorageException {
        return userDataAccess.getNumUsers();
    }
}
