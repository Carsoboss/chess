package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameController {

    public Object handleCreateGame(Request req, Response res) {
        GameService gameService = new GameService();
        String authToken = req.headers("Authorization");
        String gameName = new Gson().fromJson(req.body(), CreateGameRequest.class).getGameName();

        try {
            GameData gameResponse = gameService.createGame(gameName, authToken);
            res.status(200);
            return new Gson().toJson(gameResponse);
        } catch (DataAccessException e) {
            res.status(400);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
