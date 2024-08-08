package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import requests.CreateGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameController {

    private final GameService gameService;

    public CreateGameController(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleCreateGame(Request req, Response res) {
        String authToken = req.headers("Authorization");
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);

        try {
            GameData gameResponse = gameService.createGame(createGameRequest.getGameName(), authToken);
            res.status(200);
            return new Gson().toJson(gameResponse);
        } catch (DataAccessException e) {
            System.err.println("Error creating game: " + e.getMessage());
            if (e.getMessage().contains("Game name already taken")) {
                res.status(403);
            } else if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
            } else {
                res.status(400);
            }
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
