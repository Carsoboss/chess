package server;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        port(desiredPort);

        // Configure static files location
        staticFiles.location("/web");

        // Register endpoints
        get("/", (req, res) -> {
            res.type("text/html");
            return "<h1>Welcome to the 240 Chess Server</h1>";
        });

        // Additional example endpoint
        get("/hello", (req, res) -> "Hello, World!");

        // Initialize the server
        init();

        awaitInitialization();
        return port();
    }

    public void stop() {
        stop();
        awaitStop();
    }
}
