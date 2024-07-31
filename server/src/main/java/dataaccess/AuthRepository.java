package dataaccess;

import model.AuthData;
import exception.DataException;

public interface AuthRepository {
    AuthData generateAuthToken(String username) throws DataException;
    AuthData findAuthByToken(String authToken) throws DataException;
    AuthData findAuthByUsername(String username) throws DataException;
    void removeAuth(String authToken) throws DataException;
    void clearAllAuths() throws DataException;
    int countAuths() throws DataException;
}
