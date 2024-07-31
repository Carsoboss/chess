package dataaccess;

import model.UserData;

public interface IUserDAO {
    UserData getUser(String username) throws DAOException;
    void addUser(UserData user) throws DAOException;
    void deleteAllUsers() throws DAOException;

    void removeAllUsers();

    int getUserCount() throws DAOException;
}
