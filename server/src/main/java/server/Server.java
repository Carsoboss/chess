package server;

import controller.*;
import service.UserService;
import service.GameService;
import service.ClearService;
import spark.Spark;

public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    // Constructor that accepts the services as arguments
    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;
    }

    public int run(int desiredPort) {
        try {
            Spark.port(desiredPort);

            // Serve static files from the /web directory in src/main/resources
            Spark.staticFiles.location("web");

            // Register routes and handlers
            Spark.post("/user", (req, res) -> new RegisterController(userService).handleRegister(req, res));
            Spark.post("/session", (req, res) -> new LoginController(userService).handleLogin(req, res));
            Spark.delete("/session", (req, res) -> new LogoutController(userService).handleLogout(req, res));
            Spark.get("/game", (req, res) -> new ListGamesController(gameService).handleListGames(req, res));
            Spark.post("/game", (req, res) -> new CreateGameController(gameService).handleCreateGame(req, res));
            Spark.put("/game", (req, res) -> new JoinGameController(gameService).handleJoinGame(req, res));
            Spark.delete("/db", (req, res) -> new ClearDatabaseController(clearService).handleClearDatabase(req, res));

            // Handle unsupported routes
            Spark.notFound((req, res) -> {
                res.type("application/json");
                res.status(404);
                return "{\"message\":\"Route not found\"}";
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
