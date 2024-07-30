package server;

import com.google.gson.Gson;
import dataaccess.DAOFactory;
import requestresult.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private final GameService gameService;
    private final UserService userService;
    private final ClearService clearService;
    private static final int SUCCESS_STATUS = 200;

    public Server() {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.userService = new UserService();
        this.gameService = new GameService();
        this.clearService = new ClearService();
    }

    public int run(int port) {
        Spark.port(port);
        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(ServiceException.class, this::handleException);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void handleException(ServiceException exception, Request request, Response response) {
        int statusCode;
        switch (exception.getMessage()) {
            case "Game not found" -> statusCode = 400;
            case "Unauthorized" -> statusCode = 401;
            case "Position already taken" -> statusCode = 403;
            default -> statusCode = 500;
        }
        response.status(statusCode);
        response.body(new Gson().toJson(Map.of("message", "Error: " + exception.getMessage())));
    }

    private Object register(Request request, Response response) throws ServiceException {
        RegisterUserRequest registerRequest = new Gson().fromJson(request.body(), RegisterUserRequest.class);
        RegisterUserResponse registerResponse = userService.register(registerRequest);
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(registerResponse);
    }

    private Object listGames(Request request, Response response) throws ServiceException {
        ListGamesRequest listRequest = new ListGamesRequest(request.headers("authorization"));
        ListGamesResponse listResponse = gameService.list(listRequest);
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(listResponse);
    }

    private Object clear(Request request, Response response) throws ServiceException {
        ClearResponse clearResponse = clearService.clear();
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(clearResponse);
    }

    private Object login(Request request, Response response) throws ServiceException {
        LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        LoginResponse loginResponse = userService.login(loginRequest);
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(loginResponse);
    }

    private Object logout(Request request, Response response) throws ServiceException {
        LogoutRequest logoutRequest = new LogoutRequest(request.headers("authorization"));
        LogoutResponse logoutResponse = userService.logout(logoutRequest);
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(logoutResponse);
    }

    private Object createGame(Request request, Response response) throws ServiceException {
        String authToken = request.headers("authorization");
        CreateGameRequest createRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
        CreateGameRequest newRequest = new CreateGameRequest(authToken, createRequest.getGameName());
        CreateGameResponse createResponse = gameService.create(newRequest);
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(createResponse);
    }

    private Object joinGame(Request request, Response response) throws ServiceException {
        JoinGameRequest joinRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
        String authToken = request.headers("authorization");
        JoinGameRequest newRequest = new JoinGameRequest(authToken, joinRequest.getPlayerColor(), joinRequest.getGameId());
        JoinGameResponse joinResponse = gameService.join(newRequest);
        response.status(SUCCESS_STATUS);
        return new Gson().toJson(joinResponse);
    }
}
