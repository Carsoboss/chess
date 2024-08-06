package client;

import model.AuthData;
import model.GameData;
import model.UserData;  // <-- Make sure this import is correct
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final UserData EXISTING_USER = new UserData("existingUsername", "existingPassword", "existingEmail");
    private static String existingAuth;
    private static final UserData NEW_USER = new UserData("newUsername", "newPassword", "newEmail");

    @BeforeAll
    public static void init() throws IOException {
        server = new Server();
        server.run(SERVER_PORT);
        System.out.println("Started test HTTP server on " + SERVER_PORT);
        facade = new ServerFacade(SERVER_HOST, SERVER_PORT);
        facade.clearDatabase();
        AuthData authData = facade.register(EXISTING_USER.username(), EXISTING_USER.password(), EXISTING_USER.email());
        existingAuth = authData.authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // login
    @Test
    public void loginNormal() throws IOException {
        AuthData authData = facade.login(EXISTING_USER.username(), EXISTING_USER.password());
        assertNotNull(authData);
        assertTrue(authData.authToken().length() >= 10);
    }

    @Test
    public void loginBadPassword() throws IOException {
        AuthData authData = facade.login(EXISTING_USER.username(), NEW_USER.password());
        assertNull(authData);
    }

    // register
    @Test
    public void registerNormal() throws IOException {
        AuthData authData = facade.register(NEW_USER.username(), NEW_USER.password(), NEW_USER.email());
        assertEquals(NEW_USER.username(), authData.username());
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerExistingUser() throws IOException {
        AuthData authData = facade.register(EXISTING_USER.username(), EXISTING_USER.password(), EXISTING_USER.email());
        assertNull(authData);
    }

    // logout
    @Test
    public void logoutNormal() throws IOException {
        facade.logout(existingAuth);
        AuthData authData = facade.login(EXISTING_USER.username(), EXISTING_USER.password());
        assertNotNull(authData);
    }

    @Test
    public void logoutFakeAuth() throws IOException {
        facade.logout("fake-auth-token");
    }

    // list
    @Test
    public void listNormal() throws IOException {
        GameData[] games = facade.listGames(existingAuth);
        assertNotNull(games);
    }

    @Test
    public void listBadAuth() throws IOException {
        GameData[] games = facade.listGames("fake-auth");
        assertNull(games);
    }

    // create
    @Test
    public void createNormal() throws IOException {
        GameData gameData = facade.createGame(existingAuth, "newGame");
        assertNotNull(gameData);
        assertTrue(gameData.gameID() >= 1);
    }

    @Test
    public void createBadAuth() throws IOException {
        GameData gameData = facade.createGame("bad-auth", "newGame");
        assertNull(gameData);
    }

    // join
    @Test
    public void joinNormal() throws IOException {
        GameData gameData = facade.createGame(existingAuth, "testGame");
        GameData joinedGame = facade.joinGame(existingAuth, "WHITE", gameData.gameID());
        assertNotNull(joinedGame);
    }

    @Test
    public void joinNonexistentGame() throws IOException {
        GameData joinedGame = facade.joinGame(existingAuth, "WHITE", -1);
        assertNull(joinedGame);
    }

    // clear
    @Test
    public void clearDatabase() throws IOException {
        facade.clearDatabase();
        GameData[] games = facade.listGames(existingAuth);
        assertEquals(0, games.length);
    }
}
