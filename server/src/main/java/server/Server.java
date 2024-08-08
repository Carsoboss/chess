package server;

import controller.*;
import dataaccess.*;
import service.UserService;
import service.GameService;
import service.ClearService;
import spark.Spark;

public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;
    }

    public Server() {
        UserDataAccess userDataAccess = new MySQLUserDataAccess();
        AuthDataAccess authDataAccess = new MySQLAuthDataAccess();
        GameDataAccess gameDataAccess = new MySQLGameDataAccess();
        this.userService = new UserService(userDataAccess, authDataAccess);
        this.gameService = new GameService(authDataAccess, gameDataAccess);
        this.clearService = new ClearService(userDataAccess, authDataAccess, gameDataAccess);
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTablesIfNotExists();

            Spark.port(desiredPort);
            Spark.staticFiles.location("web");

            Spark.post("/user", (req, res) -> new RegisterController(userService).handleRegister(req, res));
            Spark.post("/session", (req, res) -> new LoginController(userService).handleLogin(req, res));
            Spark.delete("/session", (req, res) -> new LogoutController(userService).handleLogout(req, res));
            Spark.get("/game", (req, res) -> new ListGamesController(gameService).handleListGames(req, res));
            Spark.post("/game", (req, res) -> new CreateGameController(gameService).handleCreateGame(req, res));
            Spark.put("/game", (req, res) -> new JoinGameController(gameService).handleJoinGame(req, res));
            Spark.delete("/db", (req, res) -> new ClearDatabaseController(clearService).handleClearDatabase(req, res));

            Spark.exception(DataAccessException.class, (ex, req, res) -> {
                if (ex.getMessage().contains("Unauthorized")) {
                    res.status(401);
                } else if (ex.getMessage().contains("Username already taken")) {
                    res.status(403);
                } else if (ex.getMessage().contains("bad request")) {
                    res.status(400);
                } else {
                    res.status(500);
                }
                res.body("{\"message\":\"" + ex.getMessage() + "\"}");
            });

            Spark.awaitInitialization();
            return Spark.port();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
            Spark.stop();
            return -1;
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
