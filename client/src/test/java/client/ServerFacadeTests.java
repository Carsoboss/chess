package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static final UserData EXISTING_USER = new UserData("existingUsername", "existingPassword", "existingEmail");
    private static String existingAuth;
    private static final UserData NEW_USER = new UserData("newUsername", "newPassword", "newEmail");

    @BeforeAll
    public static void init() throws IOException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port); // Updated the constructor usage
        facade.clearDatabase();
        facade.register(EXISTING_USER.username(), EXISTING_USER.password(), EXISTING_USER.email());
        existingAuth = facade.login(EXISTING_USER.username(), EXISTING_USER.password()).authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // login
    @Test
    public void loginNormal() throws IOException {
        AuthData result = facade.login(EXISTING_USER.username(), EXISTING_USER.password());
        assertNotNull(result);
        assertTrue(result.authToken().length() >= 10);
    }

    @Test
    public void loginBadPassword() throws IOException {
        AuthData result = facade.login(EXISTING_USER.username(), NEW_USER.password());
        assertNull(result);
    }

    // register
    @Test
    public void registerNormal() throws IOException {
        AuthData result = facade.register(NEW_USER.username(), NEW_USER.password(), NEW_USER.email());
        assertEquals(NEW_USER.username(), result.username());
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void registerExistingUser() throws IOException {
        AuthData result = facade.register(EXISTING_USER.username(), EXISTING_USER.password(), EXISTING_USER.email());
        assertNull(result);
    }

    // logout
    @Test
    public void logoutNormal() throws IOException {
        facade.logout(existingAuth);
        assertNull(facade.login(EXISTING_USER.username(), EXISTING_USER.password()));
    }

    @Test
    public void logoutFakeAuth() throws IOException {
        assertThrows(IOException.class, () -> facade.logout("fake-auth-token"));
    }

    // list
    @Test
    public void listNormal() throws IOException {
        existingAuth = facade.login(EXISTING_USER.username(), EXISTING_USER.password()).authToken();
        facade.createGame(existingAuth, "funGame");
        GameData[] result = facade.listGames(existingAuth);  // `GameData` is used here
        assertTrue(result.length > 0);
    }

    @Test
    public void listBadAuth() throws IOException {
        assertThrows(IOException.class, () -> facade.listGames("fake-auth"));
    }

    // create
    @Test
    public void createNormal() throws IOException {
        GameData result = facade.createGame(existingAuth, "newGame");  // `GameData` is used here
        assertNotNull(result);
        assertTrue(result.gameID() >= 1);
    }

    @Test
    public void createBadAuth() throws IOException {
        assertThrows(IOException.class, () -> facade.createGame("bad-auth", "newGame"));
    }

    // join
    @Test
    public void joinNormal() throws IOException {
        facade.joinGame(existingAuth, "WHITE", 1);
    }

    @Test
    public void joinNonexistentGame() throws IOException {
        assertThrows(IOException.class, () -> facade.joinGame(existingAuth, "WHITE", -1));
    }

    // clear
    @Test
    public void clearDatabase() {
        assertDoesNotThrow(() -> facade.clearDatabase());
    }
}
