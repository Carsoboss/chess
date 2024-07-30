package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface IGameDAO {
    GameData createGame(String gameName);
    void deleteAllGames() throws DAOException;
    List<GameData> getAllGames() throws DAOException;
    void addPlayerToGame(ChessGame.TeamColor color, int gameId, String username) throws DAOException;
}
