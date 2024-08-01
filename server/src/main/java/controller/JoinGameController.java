package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameController {

    private final GameService gameService;

    public JoinGameController(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleJoinGame(Request req, Response res) {
        String authToken = req.headers("Authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);

        try {
            GameData gameResponse = gameService.joinGame(joinGameRequest.getPlayerColor(), joinGameRequest.getGameID(), authToken);
            res.status(200);
            return new Gson().toJson(gameResponse);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already assigned")) {
                res.status(403); // Forbidden
            } else if (e.getMessage().contains("Unauthorized")) {
                res.status(401); // Unauthorized
            } else {
                res.status(400); // Bad request
            }
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}


