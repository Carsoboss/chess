package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import requests.JoinGameRequest;
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

        System.out.println("JoinGameRequest received: " + joinGameRequest);
        System.out.println("AuthToken: " + authToken);

        try {
            GameData gameResponse = gameService.joinGame(joinGameRequest.getPlayerColor(), joinGameRequest.getGameID(), authToken);
            res.status(200);
            return new Gson().toJson(gameResponse);
        } catch (DataAccessException e) {
            System.err.println("Error joining game: " + e.getMessage());
            if (e.getMessage().contains("already assigned")) {
                res.status(403);
            } else if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
            } else if (e.getMessage().contains("Invalid player color")) {
                res.status(400);
            } else {
                res.status(400);
            }
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }

}
