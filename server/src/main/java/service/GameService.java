package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.StorageException;
import model.AuthData;
import model.GameData;
import requestresult.*;

import java.util.Collection;

public class GameService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public GameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public CreateGameResponse create(CreateGameRequest request) throws StorageException {
        AuthData auth = authDataAccess.retrieveAuthByAuthToken(request.getAuthToken());
        if (auth == null) {
            throw new StorageException("Unauthorized");
        }
        GameData game = gameDataAccess.createGame(request.getGameName());
        return new CreateGameResponse(game.gameID());
    }

    public JoinGameResponse join(JoinGameRequest request) throws StorageException {
        AuthData auth = authDataAccess.retrieveAuthByAuthToken(request.getAuthToken());
        if (auth == null) {
            throw new StorageException("Unauthorized");
        }
        gameDataAccess.joinGame(request.getPlayerColor(), request.getGameId(), auth.username());
        return new JoinGameResponse();
    }

    public ListGamesResponse list(ListGamesRequest request) throws StorageException {
        AuthData auth = authDataAccess.retrieveAuthByAuthToken(request.getAuthToken());
        if (auth == null) {
            throw new StorageException("Unauthorized");
        }
        Collection<GameData> games = gameDataAccess.listAllGames();
        return new ListGamesResponse(games);
    }
}
