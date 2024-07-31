package server;

import controller.*;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.post("/user", (req, res) -> new RegisterController().handleRegister(req, res));
        Spark.post("/session", (req, res) -> new LoginController().handleLogin(req, res));
        Spark.delete("/session", (req, res) -> new LogoutController().handleLogout(req, res));
        Spark.get("/game", (req, res) -> new ListGamesController().handleListGames(req, res));
        Spark.post("/game", (req, res) -> new CreateGameController().handleCreateGame(req, res));
        Spark.put("/game", (req, res) -> new JoinGameController().handleJoinGame(req, res));
        Spark.delete("/db", (req, res) -> new ClearDatabaseController().handleClearDatabase(req, res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
