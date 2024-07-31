import server.Server;
import service.UserService;
import service.GameService;
import service.ClearService;
import dataaccess.InMemoryUserDataAccess;
import dataaccess.InMemoryAuthDataAccess;
import dataaccess.InMemoryGameDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;

public class Main {
    public static void main(String[] args) {
        // Initialize your data access objects here
        UserDataAccess userDataAccess = new InMemoryUserDataAccess();
        AuthDataAccess authDataAccess = new InMemoryAuthDataAccess();
        GameDataAccess gameDataAccess = new InMemoryGameDataAccess();

        // Initialize services with DAOs
        UserService userService = new UserService(userDataAccess, authDataAccess);
        GameService gameService = new GameService(authDataAccess, gameDataAccess);
        ClearService clearService = new ClearService(userDataAccess, authDataAccess, gameDataAccess);

        // Pass the services to the server
        Server serverInstance = new Server(userService, gameService, clearService);

        // Start the server on port 8080
        serverInstance.run(8080);
    }
}
