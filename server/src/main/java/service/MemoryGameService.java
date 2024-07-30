package service;

import dataaccess.DAOFactory;
import dataaccess.ServiceException;
import model.AuthData;
import model.GameData;
import requestresult.*;
import java.util.Collection;

public class MemoryGameService implements IGameService {
    private final DAOFactory daoFactory;

    public MemoryGameService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public ListGamesResponse listGames(ListGamesRequest request) throws ServiceException {
        validateRequest(request);
        authenticateUser(request.getAuthToken());

        Collection<GameData> games = daoFactory.getGameDAO().getAllGames();
        return new ListGamesResponse(games);
    }

    @Override
    public CreateGameResponse createGame(CreateGameRequest request) throws ServiceException {
        validateRequest(request);
        validateString(request.getGameName());

        authenticateUser(request.getAuthToken());

        GameData game = daoFactory.getGameDAO().createGame(request.getGameName());
        return new CreateGameResponse(game.getGameID());
    }

    @Override
    public JoinGameResponse joinGame(JoinGameRequest request) throws ServiceException {
        validateRequest(request);
        validateObject(request.getPlayerColor());
        validateObject(request.getGameId());

        AuthData authData = authenticateUser(request.getAuthToken());

        daoFactory.getGameDAO().addPlayerToGame(request.getPlayerColor(), request.getGameId(), authData.getUsername());
        return new JoinGameResponse();
    }

    private void validateRequest(Object request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }
    }

    private void validateString(String value) throws ServiceException {
        if (value == null || value.isEmpty()) {
            throw new ServiceException("Invalid value");
        }
    }

    private void validateObject(Object value) throws ServiceException {
        if (value == null) {
            throw new ServiceException("Invalid value");
        }
    }

    private AuthData authenticateUser(String authToken) throws ServiceException {
        try {
            AuthData authData = daoFactory.getAuthDAO().getAuthToken(authToken);
            if (authData == null) {
                throw new ServiceException("Unauthorized");
            }
            return authData;
        } catch (Exception e) {
            throw new ServiceException("Error authenticating user", e);
        }
    }
}
