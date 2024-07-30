package dataaccess;

import model.AuthData;

public interface IAuthDAO {
    AuthData createAuthToken(String username) throws DAOException;
    AuthData getAuthToken(String authToken) throws DAOException;
    void removeAuthToken(String authToken) throws DAOException;
    void removeAllAuthTokens() throws DAOException;
    int getAuthTokenCount() throws DAOException;
}
