import serverfacade.ServerFacade;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static boolean finished;
    private static ServerFacade facade;
    private static Scanner scanner;
    private static String authToken;
    private static GameData[] currentGames;

    public static void main(String[] args) {
        String serverURL = "http://localhost:8080";
        if (args.length > 0) {
            serverURL = args[0];
        }

        facade = new ServerFacade(serverURL);
        scanner = new Scanner(System.in);

        System.out.println("Welcome to Chess Client. Type 'help' to get started.");

        finished = false;
        while (!finished) {
            System.out.print("Enter command: ");
            String userOption = scanner.nextLine();

            switch (userOption.toLowerCase()) {
                case "help" -> handleHelp();
                case "quit" -> handleQuit();
                case "login" -> handleLogin();
                case "register" -> handleRegister();
                case "logout" -> handleLogout();
                case "create" -> handleCreateGame();
                case "list" -> handleListGames();
                case "join" -> handleJoinGame();
                default -> System.out.println("Unrecognized command. Type 'help' for a list of commands.");
            }
        }
    }

    private static void handleHelp() {
        if (authToken == null) {
            System.out.println("""
                    Available commands:
                    help - Show this help message
                    register - Register a new user
                    login - Log in as an existing user
                    quit - Exit the application
                    """);
        } else {
            System.out.println("""
                    Available commands:
                    help - Show this help message
                    logout - Log out the current user
                    create - Create a new game
                    list - List all available games
                    join - Join an existing game
                    quit - Exit the application
                    """);
        }
    }

    private static void handleQuit() {
        System.out.println("Thanks for playing!");
        finished = true;
    }

    private static void handleLogin() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            AuthData auth = facade.login(username, password);
            authToken = auth.authToken();

            System.out.println("Successfully logged in as " + username);
        } catch (IOException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void handleRegister() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            AuthData auth = facade.register(username, password, email);
            authToken = auth.authToken();

            System.out.println("Successfully registered and logged in as " + username);
        } catch (IOException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        try {
            facade.logout(authToken);
            authToken = null;
            currentGames = null;
            System.out.println("Successfully logged out.");
        } catch (IOException e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private static void handleCreateGame() {
        if (authToken == null) {
            System.out.println("You must be logged in to create a game.");
            return;
        }

        try {
            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine();

            facade.createGame(gameName, authToken);
            System.out.println("Game '" + gameName + "' created successfully.");
        } catch (IOException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private static void handleListGames() {
        if (authToken == null) {
            System.out.println("You must be logged in to list games.");
            return;
        }

        try {
            currentGames = facade.listGames(authToken);
            if (currentGames.length == 0) {
                System.out.println("No available games.");
            } else {
                for (int i = 0; i < currentGames.length; i++) {
                    System.out.println((i + 1) + ". " + currentGames[i].gameName());
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private static void handleJoinGame() {
        if (authToken == null) {
            System.out.println("You must be logged in to join a game.");
            return;
        }

        if (currentGames == null || currentGames.length == 0) {
            System.out.println("No games available to join. Please list games first.");
            return;
        }

        try {
            System.out.print("Enter game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter player color (WHITE or BLACK): ");
            String playerColor = scanner.nextLine();

            GameData game = currentGames[gameNumber - 1];
            facade.joinGame(game.gameID(), playerColor, authToken);
            System.out.println("Successfully joined the game: " + game.gameName());
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }
}
