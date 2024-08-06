import model.AuthData;
import model.GameData;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static ServerFacade facade;
    private static String authToken;
    private static Scanner scanner;

    public static void main(String[] args) {
        facade = new ServerFacade("localhost", 8080);
        scanner = new Scanner(System.in);

        System.out.println("Welcome to the Chess Client");
        boolean running = true;
        while (running) {
            System.out.println("Enter a command (register, login, createGame, listGames, joinGame, logout, clearDatabase, quit): ");
            String command = scanner.nextLine().trim().toLowerCase();

            try {
                switch (command) {
                    case "register" -> handleRegister();
                    case "login" -> handleLogin();
                    case "logout" -> handleLogout();
                    case "creategame" -> handleCreateGame();
                    case "listgames" -> handleListGames();
                    case "joingame" -> handleJoinGame();
                    case "cleardatabase" -> handleClearDatabase();
                    case "quit" -> {
                        System.out.println("Exiting...");
                        running = false;
                    }
                    default -> System.out.println("Unknown command. Please try again.");
                }
            } catch (IOException e) {
                System.err.println("Error occurred: " + e.getMessage());
            }
        }
    }

    private static void handleRegister() throws IOException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        AuthData authData = facade.register(username, password, email);
        if (authData != null) {
            authToken = authData.authToken();
            System.out.println("Registered and logged in as " + authData.username());
        } else {
            System.out.println("Registration failed.");
        }
    }

    private static void handleLogin() throws IOException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        AuthData authData = facade.login(username, password);
        if (authData != null) {
            authToken = authData.authToken();
            System.out.println("Logged in as " + authData.username());
        } else {
            System.out.println("Login failed.");
        }
    }

    private static void handleLogout() throws IOException {
        facade.logout(authToken);
        authToken = null;
        System.out.println("Logged out.");
    }

    private static void handleCreateGame() throws IOException {
        System.out.print("Game name: ");
        String gameName = scanner.nextLine();

        GameData gameData = facade.createGame(authToken, gameName);
        if (gameData != null) {
            System.out.println("Created game " + gameData.gameName());
        } else {
            System.out.println("Failed to create game.");
        }
    }

    private static void handleListGames() throws IOException {
        GameData[] games = facade.listGames(authToken);
        if (games != null && games.length > 0) {
            System.out.println("Available games:");
            for (GameData game : games) {
                System.out.println("Game ID: " + game.gameID() + ", Name: " + game.gameName());
            }
        } else {
            System.out.println("No games available.");
        }
    }

    private static void handleJoinGame() throws IOException {
        System.out.print("Game ID: ");
        int gameId = Integer.parseInt(scanner.nextLine());
        System.out.print("Player color (WHITE/BLACK): ");
        String playerColor = scanner.nextLine().toUpperCase();

        GameData gameData = facade.joinGame(authToken, playerColor, gameId);
        if (gameData != null) {
            System.out.println("Joined game " + gameData.gameName() + " as " + playerColor);
        } else {
            System.out.println("Failed to join game.");
        }
    }

    private static void handleClearDatabase() throws IOException {
        facade.clearDatabase();
        System.out.println("Database cleared.");
    }
}
