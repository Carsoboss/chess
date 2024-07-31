package service;

import dataaccess.DAOFactory;
import dataaccess.ServiceException;
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
        validateRequest(request);
        validateString(request.username());
        validateString(request.password());
        validateString(request.email());

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData newUser = new UserData(request.username(), hashedPassword, request.email());

        try {
            daoFactory.getUserDAO().addUser(newUser);
            AuthData newAuth = daoFactory.getAuthDAO().createAuthToken(request.username());
            return new RegisterUserResponse(request.username(), newAuth.authToken());
        } catch (Exception e) {
            throw new ServiceException("Error registering user", e);
        }
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) throws ServiceException {
        validateRequest(request);
        validateString(request.username());
        validateString(request.password());

        try {
            UserData user = daoFactory.getUserDAO().getUser(request.username());
            if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
                throw new ServiceException("Unauthorized");
            }

            AuthData authData = daoFactory.getAuthDAO().createAuthToken(user.username());
            return new LoginResponse(user.username(), authData.authToken());
        } catch (Exception e) {
            throw new ServiceException("Error logging in user", e);
        }
    }

    @Override
    public LogoutResponse logoutUser(LogoutRequest request) throws ServiceException {
        validateRequest(request);
        try {
            authenticateUser(request.authToken());
            daoFactory.getAuthDAO().removeAuthToken(request.authToken());
            return new LogoutResponse();
        } catch (Exception e) {
            throw new ServiceException("Error logging out user", e);
        }
    }

    private void validateRequest(Object request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }
    }

    private void validateString(String value) throws ServiceException {
        if (value == null || value.isEmpty()) {
            throw new ServiceException("Invalid value");
        }
    }

    private AuthData authenticateUser(String authToken) throws ServiceException {
        try {
            AuthData authData = daoFactory.getAuthDAO().getAuthToken(authToken);
            if (authData == null) {
                throw new ServiceException("Unauthorized");
            }
            return authData;
        } catch (Exception e) {
            throw new ServiceException("Error authenticating user", e);
        }
    }
}
