package client;

import model.AuthData;
import model.GameData;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static boolean isRunning;
    private static AppState state = AppState.PRELOGIN;
    private static ServerFacade facade;
    private static Scanner scanner;
    private static String authToken;
    private static Map<Integer, GameData> currentGames;

    public static void main(String[] args) {
        int port = 8080;
        facade = new ServerFacade("localhost", port);
        scanner = new Scanner(System.in);
        currentGames = new HashMap<>();

        System.out.println("Welcome to your Custom Chess Client. Type 'help' to get started.");

        isRunning = true;
        while (isRunning) {
            System.out.print("Enter a command: ");
            String command = scanner.nextLine().trim().toLowerCase();
            processCommand(command);
        }
    }

    private static void processCommand(String command) {
        try {
            switch (command) {
                case "help" -> displayHelp();
                case "quit" -> quitApplication();
                case "register" -> handleRegister();
                case "login" -> handleLogin();
                case "logout" -> handleLogout();
                case "creategame" -> handleCreateGame();
                case "listgames" -> handleListGames();
                case "joingame" -> handleJoinGame();
                case "cleardatabase" -> handleClearDatabase();
                default -> System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void displayHelp() {
        String helpText = switch (state) {
            case PRELOGIN -> """
                Available commands:
                - register: Register a new user
                - login: Log in with an existing user
                - quit: Exit the application
                - help: Display this help message
                """;
            case POSTLOGIN -> """
                Available commands:
                - logout: Log out of your account
                - creategame: Create a new chess game
                - listgames: List all available chess games
                - joingame: Join an existing chess game
                - cleardatabase: Clear the game database
                - quit: Exit the application
                - help: Display this help message
                """;
            default -> "Unexpected state. Please restart the application.";
        };
        System.out.println(helpText);
    }

    private static void quitApplication() {
        System.out.println("Thank you for using the Chess Client. Goodbye!");
        isRunning = false;
    }

    private static void handleRegister() throws IOException {
        if (state != AppState.PRELOGIN) {
            System.out.println("You must be logged out to register.");
            return;
        }

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Enter an email: ");
        String email = scanner.nextLine();

        AuthData authData = facade.register(username, password, email);
        if (authData != null) {
            authToken = authData.authToken();
            state = AppState.POSTLOGIN;
            System.out.println("Registration successful. Logged in as " + authData.username());
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private static void handleLogin() throws IOException {
        if (state != AppState.PRELOGIN) {
            System.out.println("You are already logged in.");
            return;
        }

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        AuthData authData = facade.login(username, password);
        if (authData != null) {
            authToken = authData.authToken();
            state = AppState.POSTLOGIN;
            System.out.println("Login successful. Welcome back, " + authData.username());
        } else {
            System.out.println("Login failed. Please check your credentials and try again.");
        }
    }

    private static void handleLogout() throws IOException {
        if (state != AppState.POSTLOGIN) {
            System.out.println("You must be logged in to log out.");
            return;
        }

        facade.logout(authToken);
        authToken = null;
        state = AppState.PRELOGIN;
        System.out.println("You have been logged out.");
    }

    private static void handleCreateGame() throws IOException {
        if (state != AppState.POSTLOGIN) {
            System.out.println("You must be logged in to create a game.");
            return;
        }

        System.out.print("Enter a name for the game: ");
        String gameName = scanner.nextLine();

        GameData gameData = facade.createGame(authToken, gameName);
        if (gameData != null) {
            System.out.println("Game created: " + gameData.gameName());
        } else {
            System.out.println("Failed to create game. Please try again.");
        }
    }

    private static void handleListGames() throws IOException {
        if (state != AppState.POSTLOGIN) {
            System.out.println("You must be logged in to list games.");
            return;
        }

        GameData[] games = facade.listGames(authToken);
        if (games != null && games.length > 0) {
            System.out.println("Available games:");
            for (int i = 0; i < games.length; i++) {
                GameData game = games[i];
                currentGames.put(i, game);
                System.out.println((i + 1) + ". " + game.gameName() + " (ID: " + game.gameID() + ")");
            }
        } else {
            System.out.println("No games available.");
        }
    }

    private static void handleJoinGame() throws IOException {
        if (state != AppState.POSTLOGIN) {
            System.out.println("You must be logged in to join a game.");
            return;
        }

        System.out.print("Enter the number of the game to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine()) - 1;

        if (currentGames.containsKey(gameNumber)) {
            GameData selectedGame = currentGames.get(gameNumber);
            System.out.print("Enter the color to play as (WHITE/BLACK): ");
            String playerColor = scanner.nextLine().toUpperCase();

            GameData gameData = facade.joinGame(authToken, playerColor, selectedGame.gameID());
            if (gameData != null) {
                System.out.println("Successfully joined the game: " + gameData.gameName());
            } else {
                System.out.println("Failed to join game. Please try again.");
            }
        } else {
            System.out.println("Invalid game number.");
        }
    }

    private static void handleClearDatabase() throws IOException {
        if (state != AppState.POSTLOGIN) {
            System.out.println("You must be logged in to clear the database.");
            return;
        }

        facade.clearDatabase();
        System.out.println("Database cleared successfully.");
    }

    private enum AppState {
        PRELOGIN,
        POSTLOGIN
    }
}
