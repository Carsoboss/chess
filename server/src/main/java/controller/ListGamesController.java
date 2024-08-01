package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.List;

public class ListGamesController {

    private final GameService gameService;

    public ListGamesController(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleListGames(Request req, Response res) {
        String authToken = req.headers("Authorization");

        try {
            List<GameData> games = gameService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(games);
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}