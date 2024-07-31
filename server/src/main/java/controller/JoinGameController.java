package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;


public class JoinGameController {

    public Object handleJoinGame(Request req, Response res) {
        GameService gameService = new GameService();
        String authToken = req.headers("Authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);

        try {
            GameData gameResponse = gameService.joinGame(joinGameRequest.getPlayerColor(), joinGameRequest.getGameID(), authToken);
            res.status(200);
            return new Gson().toJson(gameResponse);
        } catch (DataAccessException e) {
            res.status(400);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
