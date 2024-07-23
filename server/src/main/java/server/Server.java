package server;

import java.nio.file.Files;
import java.nio.file.Paths;
import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        port(desiredPort);

        // Log to check if static files exist
        System.out.println("Static files should be served from: " + Paths.get("src/main/resources/web").toAbsolutePath().toString());
        System.out.println("Checking if index.html exists: " + Files.exists(Paths.get("src/main/resources/web/index.html")));
        System.out.println("Checking if index.js exists: " + Files.exists(Paths.get("src/main/resources/web/index.js")));
        System.out.println("Checking if favicon.ico exists: " + Files.exists(Paths.get("src/main/resources/web/favicon.ico")));

        // Configure static files location
        staticFiles.location("/web");

        // Redirect root path to index.html
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // Handle 404 errors
        notFound((req, res) -> {
            res.type("text/html");
            return "<h1>404 Not Found</h1>";
        });

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
