package service;

import requestresult.*;
import service.ServiceException;


public interface IGameService {
    ListGamesResponse listGames(ListGamesRequest request) throws ServiceException;
    CreateGameResponse createGame(CreateGameRequest request) throws ServiceException;
    JoinGameResponse joinGame(JoinGameRequest request) throws ServiceException;

    CreateGameResponse create(CreateGameRequest newRequest);

    JoinGameResponse join(JoinGameRequest newRequest);

    ListGamesResponse list(ListGamesRequest listRequest);
}
