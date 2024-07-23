package server;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        port(desiredPort);

        // Configure static files location
        staticFiles.location("/web");

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
