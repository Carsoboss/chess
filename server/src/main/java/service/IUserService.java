package service;

import requestresult.*;
import dataaccess.ServiceException;

public interface IUserService {
    RegisterUserResponse registerUser(RegisterUserRequest request) throws ServiceException;
    LoginResponse loginUser(LoginRequest request) throws ServiceException;
    LogoutResponse logoutUser(LogoutRequest request) throws ServiceException;
}
