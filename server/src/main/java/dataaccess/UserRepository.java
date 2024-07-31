package dataaccess;

import model.UserData;
import exception.DataException;

public interface UserRepository {
    void addUser(UserData user) throws DataException;
    UserData findUserByUsername(String username) throws DataException;
    void deleteAllUsers() throws DataException;
    int countUsers() throws DataException;
}
