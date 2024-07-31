package service;

import dataaccess.*;

public class ClearService {

    private final UserDataAccess userDataAccess = new InMemoryUserDataAccess();
    private final GameDataAccess gameDataAccess = new InMemoryGameDataAccess();
    private final AuthDataAccess authDataAccess = new InMemoryAuthDataAccess();

    public void clearDatabase() throws DataAccessException {
        userDataAccess.deleteAllUsers();
        gameDataAccess.deleteAllGames();
        authDataAccess.deleteAllAuths();
    }
}
