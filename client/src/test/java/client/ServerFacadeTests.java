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
        facade = new ServerFacade("localhost", port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterUser() throws IOException {
        AuthData authData = facade.register("testUser", "testPass", "testEmail@example.com");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
        authToken = authData.authToken();
    }

    @Test
    public void testLoginUser() throws IOException {
        AuthData authData = facade.login("testUser", "testPass");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
        authToken = authData.authToken();
    }

    @Test
    public void testCreateGame() throws IOException {
        facade.login("testUser", "testPass");
        GameData gameData = facade.createGame(authToken, "testGame");
        assertNotNull(gameData);
        assertEquals("testGame", gameData.gameName());
    }

    @Test
    public void testListGames() throws IOException {
        facade.login("testUser", "testPass");
        GameData[] games = facade.listGames(authToken);
        assertNotNull(games);
        assertTrue(games.length > 0);
    }

    @Test
    public void testJoinGame() throws IOException {
        facade.login("testUser", "testPass");
        GameData gameData = facade.createGame(authToken, "testGameToJoin");
        GameData joinedGame = facade.joinGame(authToken, "WHITE", gameData.gameID());
        assertNotNull(joinedGame);
        assertEquals("testGameToJoin", joinedGame.gameName());
        assertEquals("WHITE", joinedGame.whiteUsername());
    }

    @Test
    public void testClearDatabase() throws IOException {
        facade.clearDatabase();
        GameData[] games = facade.listGames(authToken);
        assertEquals(0, games.length);
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }
}
