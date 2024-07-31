package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.InMemoryAuthDataAccess;
import dataaccess.InMemoryGameDataAccess;
import dataaccess.InMemoryUserDataAccess;

public class ClearService {

    private final UserDataAccess userDataAccess = new InMemoryUserDataAccess();
    private final GameDataAccess gameDataAccess = new InMemoryGameDataAccess();
    private final AuthDataAccess authDataAccess = new InMemoryAuthDataAccess();

    public void clearDatabase() {
        userDataAccess.deleteAllUsers();
        gameDataAccess.deleteAllGames();
        authDataAccess.deleteAllAuths();
    }
}
