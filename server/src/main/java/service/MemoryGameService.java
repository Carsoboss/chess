package service;

import chess.ChessGame;
import dataaccess.DAOFactory;
import model.AuthData;
import model.GameData;
import requestresult.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameService implements IGameService {
    private final DAOFactory daoFactory;

    public MemoryGameService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public ListGamesResponse listGames(ListGamesRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }

        String authToken = request.getAuthToken();

        try {
            AuthData authData = daoFactory.getAuthDAO().getAuthToken(authToken);
            Collection<GameData> games = daoFactory.getGameDAO().getAllGames();
            return new ListGamesResponse(new ArrayList<>(games));
        } catch (Exception e) {
            throw new ServiceException("Error listing games", e);
        }
    }

    @Override
    public CreateGameResponse createGame(CreateGameRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }

        String authToken = request.getAuthToken();
        String gameName = request.getGameName();

        try {
            AuthData authData = daoFactory.getAuthDAO().getAuthToken(authToken);
            GameData game = daoFactory.getGameDAO().createGame(gameName);
            return new CreateGameResponse(game.getGameId());
        } catch (Exception e) {
            throw new ServiceException("Error creating game", e);
        }
    }

    @Override
    public JoinGameResponse joinGame(JoinGameRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Invalid request");
        }

        String authToken = request.getAuthToken();
        ChessGame.TeamColor playerColor = request.getPlayerColor();
        int gameId = request.getGameId();

        try {
            AuthData authData = daoFactory.getAuthDAO().getAuthToken(authToken);
            daoFactory.getGameDAO().addPlayerToGame(playerColor, gameId, authData.getUsername());
            return new JoinGameResponse();
        } catch (Exception e) {
            throw new ServiceException("Error joining game", e);
        }
    }

    @Override
    public CreateGameResponse create(CreateGameRequest newRequest) {
        return null;
    }

    @Override
    public JoinGameResponse join(JoinGameRequest newRequest) {
        return null;
    }

    @Override
    public ListGamesResponse list(ListGamesRequest listRequest) {
        return null;
    }
}


