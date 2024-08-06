package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        facade = new ServerFacade("http://localhost", port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearDatabase() throws IOException {
        facade.clearDatabase();  // Ensure the database is cleared before each test
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void registerUserTestPositive() throws IOException {
        AuthData authData = facade.register("testUser", "testPass", "testEmail@example.com");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
        authToken = authData.authToken();
    }

    @Test
    public void registerUserTestNegative() {
        assertThrows(Exception.class, () -> {
            facade.register("testUser", "testPass", "testEmail@example.com");
            facade.register("testUser", "anotherPass", "anotherEmail@example.com");
        });
    }

    @Test
    public void loginUserTestPositive() throws IOException {
        facade.register("testUser", "testPass", "testEmail@example.com");
        AuthData authData = facade.login("testUser", "testPass");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
        authToken = authData.authToken();
    }

    @Test
    public void loginUserTestNegative() {
        assertThrows(Exception.class, () -> {
            facade.login("nonExistentUser", "wrongPass");
        });
    }

    @Test
    public void logoutUserTestPositive() throws IOException {
        AuthData authData = facade.register("testUser", "testPass", "testEmail@example.com");
        String authToken = authData.authToken();
        assertDoesNotThrow(() -> facade.logout(authToken));
    }

    @Test
    public void logoutUserTestNegative() {
        assertThrows(Exception.class, () -> {
            facade.logout("invalidAuthToken");
        });
    }

    @Test
    public void createGameTestPositive() throws IOException {
        facade.register("testUser", "testPass", "testEmail@example.com");
        GameData gameData = facade.createGame(authToken, "testGame");
        assertNotNull(gameData);
        assertEquals("testGame", gameData.gameName());
    }

    @Test
    public void createGameTestNegative() {
        assertThrows(Exception.class, () -> {
            facade.createGame("invalidAuthToken", "testGame");
        });
    }

    @Test
    public void listGamesTestPositive() throws IOException {
        facade.register("testUser", "testPass", "testEmail@example.com");
        facade.createGame(authToken, "testGame1");
        facade.createGame(authToken, "testGame2");
        GameData[] games = facade.listGames(authToken);
        assertNotNull(games);
        assertEquals(2, games.length);
    }

    @Test
    public void listGamesTestNegative() {
        assertThrows(Exception.class, () -> {
            facade.listGames("invalidAuthToken");
        });
    }

    @Test
    public void joinGameTestPositive() throws IOException {
        facade.register("testUser", "testPass", "testEmail@example.com");
        GameData gameData = facade.createGame(authToken, "testGameToJoin");
        GameData joinedGame = facade.joinGame(authToken, "WHITE", gameData.gameID());
        assertNotNull(joinedGame);
        assertEquals("testGameToJoin", joinedGame.gameName());
        assertEquals("testUser", joinedGame.whiteUsername());
    }

    @Test
    public void joinGameTestNegative() {
        assertThrows(Exception.class, () -> {
            facade.joinGame("invalidAuthToken", "WHITE", 9999);
        });
    }

    @Test
    public void clearDatabaseTest() throws IOException {
        facade.register("testUser", "testPass", "testEmail@example.com");
        facade.createGame(authToken, "testGame");
        facade.clearDatabase();
        GameData[] games = facade.listGames(authToken);
        assertEquals(0, games.length);
    }
}
