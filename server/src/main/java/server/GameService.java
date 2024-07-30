package service;

import dataaccess.ServiceException;
import requestresult.*;

public class GameService {
    private final IGameService gameService;

    public GameService() {
        gameService = ServiceFactory.getInstance().getGameService();
    }

    public ListGamesResponse list(ListGamesRequest request) throws ServiceException {
        return gameService.listGames(request);
    }

    public CreateGameResponse create(CreateGameRequest request) throws ServiceException {
        return gameService.createGame(request);
    }

    public JoinGameResponse join(JoinGameRequest request) throws ServiceException {
        return gameService.joinGame(request);
    }
}
