package service;

import dataaccess.DAOFactory;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requestresult.*;

public class MemoryUserService implements IUserService {
    private final DAOFactory daoFactory;

    public MemoryUserService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }

        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        if (username == null || password == null || email == null) {
            throw new ServiceException("Invalid input");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData newUser = new UserData(username, hashedPassword, email);

        try {
            daoFactory.getUserDAO().addUser(newUser);
            AuthData newAuth = daoFactory.getAuthDAO().createAuthToken(username);
            return new RegisterUserResponse(username, newAuth.getAuthToken());
        } catch (Exception e) {
            throw new ServiceException("Error registering user", e);
        }
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }

        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || password == null) {
            throw new ServiceException("Invalid input");
        }

        try {
            UserData user = daoFactory.getUserDAO().getUser(username);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                throw new ServiceException("Unauthorized");
            }

            AuthData authData = daoFactory.getAuthDAO().createAuthToken(user.getUsername());
            return new LoginResponse(user.getUsername(), authData.getAuthToken());
        } catch (Exception e) {
            throw new ServiceException("Error logging in user", e);
        }
    }

    @Override
    public LogoutResponse logoutUser(LogoutRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }

        String authToken = request.getAuthToken();

        try {
            AuthData authData = daoFactory.getAuthDAO().getAuthToken(authToken);
            if (authData == null) {
                throw new ServiceException("Unauthorized");
            }

            daoFactory.getAuthDAO().removeAuthToken(authToken);
            return new LogoutResponse();
        } catch (Exception e) {
            throw new ServiceException("Error logging out user", e);
        }
    }
}


