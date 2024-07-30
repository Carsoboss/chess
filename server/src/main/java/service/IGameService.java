package service;

import requestresult.*;
import dataaccess.ServiceException;

public interface IGameService {
    ListGamesResponse listGames(ListGamesRequest request) throws ServiceException;
    CreateGameResponse createGame(CreateGameRequest request) throws ServiceException;
    JoinGameResponse joinGame(JoinGameRequest request) throws ServiceException;
}
