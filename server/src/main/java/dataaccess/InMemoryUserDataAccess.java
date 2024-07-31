package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserDataAccess implements UserDataAccess {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: Username already taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
