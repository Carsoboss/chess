package service;

import dataaccess.ServiceException;
import requestresult.*;

public class UserService {
    private final IUserService userService;

    public UserService() {
        userService = ServiceFactory.getInstance().getUserService();
    }

    public RegisterUserResponse register(RegisterUserRequest request) throws ServiceException {
        return userService.registerUser(request);
    }

    public LoginResponse login(LoginRequest request) throws ServiceException {
        return userService.loginUser(request);
    }

    public LogoutResponse logout(LogoutRequest request) throws ServiceException {
        return userService.logoutUser(request);
    }
}
