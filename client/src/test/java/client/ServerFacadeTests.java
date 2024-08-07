package client;

import model.AuthData;
import model.GameData;
import model.UserData; // Add this import
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException; // Add this import

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static final UserData EXISTING_USER = new UserData("existingUsername", "existingPassword", "existingEmail");
    private static String existingAuth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        assertDoesNotThrow(() -> {
            existingAuth = facade.register(EXISTING_USER.username(), EXISTING_USER.password(), EXISTING_USER.email()).authToken();
        });
    }

    @BeforeEach
    public void clearDB() {
        assertDoesNotThrow(() -> {
            facade.clear();
            existingAuth = facade.register(EXISTING_USER.username(), EXISTING_USER.password(), EXISTING_USER.email()).authToken();
        });
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void registerTestPositive() {
        assertDoesNotThrow(() -> {
            AuthData newAuth = facade.register("user1", "password", "myemail");
            assertNotNull(newAuth);
            assertNotNull(newAuth.authToken());
        });
    }

    @Test
    public void registerTestNegative() {
        assertThrows(IOException.class, () -> {
            facade.register("user1", "password", "myemail");
            facade.register("user1", "password2", "myemail2");
        });
    }

    @Test
    public void loginTestPositive() {
        assertDoesNotThrow(() -> {
            AuthData loginAuth = facade.login(EXISTING_USER.username(), EXISTING_USER.password());
            assertNotNull(loginAuth);
            assertNotNull(loginAuth.authToken());
        });
    }

    @Test
    public void loginTestNegative() {
        assertThrows(IOException.class, () -> {
            facade.login("user1", "wrongpassword");
        });
    }

//    @Test
//    public void logoutTestPositive() {
//        assertDoesNotThrow(() -> {
//            AuthData auth = facade.login(EXISTING_USER.username(), EXISTING_USER.password());
//            facade.logout(auth.authToken());
//        });
//    }

    @Test
    public void logoutTestNegative() {
        assertThrows(IOException.class, () -> {
            facade.logout("fake-auth-token");
        });
    }

    @Test
    public void createGameTestPositive() {
        assertDoesNotThrow(() -> {
            GameData newGame = facade.createGame("game1", existingAuth);
            assertNotNull(newGame);
            assertEquals("game1", newGame.gameName());
        });
    }

    @Test
    public void createGameTestNegative() {
        assertThrows(IOException.class, () -> {
            facade.createGame("game1", "fake-auth-token");
        });
    }

//    @Test
//    public void listGamesTestPositive() {
//        assertDoesNotThrow(() -> {
//            facade.createGame("game1", existingAuth);
//            GameData[] games = facade.listGames(existingAuth);
//            assertNotNull(games);
//            assertTrue(games.length > 0);
//        });
//    }

    @Test
    public void listGamesTestNegative() {
        assertThrows(IOException.class, () -> {
            facade.listGames("fake-auth-token");
        });
    }
//
//    @Test
//    public void joinGameTestPositive() {
//        assertDoesNotThrow(() -> {
//            GameData newGame = facade.createGame("game1", existingAuth);
//            facade.joinGame(newGame.gameID(), "WHITE", existingAuth);
//            GameData[] games = facade.listGames(existingAuth);
//            assertEquals(newGame.gameID(), games[0].gameID());
//        });
//    }

    @Test
    public void joinGameTestNegative() {
        assertThrows(IOException.class, () -> {
            facade.joinGame(-1, "WHITE", existingAuth); // Invalid game ID
        });
    }
}
