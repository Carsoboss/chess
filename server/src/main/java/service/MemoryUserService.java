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
        validateString(request.getUsername());
        validateString(request.getPassword());
        validateString(request.getEmail());

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        UserData newUser = new UserData(request.getUsername(), hashedPassword, request.getEmail());

        try {
            daoFactory.getUserDAO().addUser(newUser);
            AuthData newAuth = daoFactory.getAuthDAO().createAuthToken(request.getUsername());
            return new RegisterUserResponse(request.getUsername(), newAuth.getAuthToken());
        } catch (Exception e) {
            throw new ServiceException("Error registering user", e);
        }
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) throws ServiceException {
        validateRequest(request);
        validateString(request.getUsername());
        validateString(request.getPassword());

        try {
            UserData user = daoFactory.getUserDAO().getUser(request.getUsername());
            if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
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
        validateRequest(request);
        try {
            authenticateUser(request.getAuthToken());
            daoFactory.getAuthDAO().removeAuthToken(request.getAuthToken());
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
