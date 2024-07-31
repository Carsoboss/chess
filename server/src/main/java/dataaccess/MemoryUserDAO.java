package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements IUserDAO {
    private int nextId = 1;
    private final Map<Integer, UserData> users = new HashMap<>();

    @Override
    public void addUser(UserData user) throws DAOException {
        for (UserData currUser : users.values()) {
            if (currUser.username().equals(user.username())) {
                throw new DAOException("Username already taken");
            }
        }
        users.put(nextId++, user);
    }

    @Override
    public void deleteAllUsers() throws DAOException {

    }

    @Override
    public UserData getUser(String username) throws DAOException {
        for (UserData user : users.values()) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void removeAllUsers() {
        users.clear();
    }

    @Override
    public int getUserCount() {
        return users.size();
    }
}
