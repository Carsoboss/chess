package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData validateAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public void clear() {
        authDAO.clear();
    }
}
