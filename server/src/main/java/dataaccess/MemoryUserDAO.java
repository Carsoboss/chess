package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements IUserDAO {
    private int nextId = 1;
    private final Map<Integer, UserData> users = new HashMap<>();

    @Override
    public void addUser(UserData user) throws DAOException {
        if (users.values().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            throw new DAOException("Username already taken");
        }
        users.put(nextId++, user);
    }

    @Override
    public UserData getUser(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public int getUserCount() {
        return users.size();
    }
}
