package dataaccess;

import model.UserData;

public interface UserDataAccess {
    void addUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
    int getNumUsers() throws DataAccessException;
}
