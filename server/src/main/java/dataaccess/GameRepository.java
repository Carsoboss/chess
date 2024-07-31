package dataaccess;

import model.GameData;
import chess.ChessGame;
import exception.DataException;

import java.util.List;

public interface GameRepository {
    GameData createGame(String gameName) throws DataException;
    void deleteAllGames() throws DataException;
    List<GameData> findAllGames() throws DataException;
    void addPlayerToGame(ChessGame.TeamColor color, int gameId, String username) throws DataException;
}
