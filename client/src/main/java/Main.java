package client;

import model.GameData;
import java.io.IOException;
import java.util.*;

public class Main {
    private static boolean running = true;
    private static ServerFacade facade;
    private static Scanner scanner = new Scanner(System.in);
    private static String authToken;
    private static Map<Integer, GameData> gameList;

    public static void main(String[] args) {
        facade = new ServerFacade("localhost", 8080);

        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim().toLowerCase();

            try {
                switch (choice) {
                    case "register" -> registerUser();
                    case "login" -> loginUser();
                    case "logout" -> logoutUser();
                    case "create" -> createGame();
                    case "list" -> listGames();
                    case "play" -> playGame();
                    case "clear" -> clearDatabase();
                    case "quit" -> quit();
                    default -> System.out.println("Unknown command. Type 'help' for the list of commands.");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayMenu() {
        System.out.println("Commands: register, login, logout, create, list, play, clear, quit");
        System.out.print("> ");
    }

    private static void registerUser() throws IOException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        var authData = facade.register(username, password, email);
        authToken = authData.authToken();
        System.out.println("Registered and logged in as " + authData.username());
    }

    private static void loginUser() throws IOException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        var authData = facade.login(username, password);
        authToken = authData.authToken();
        System.out.println("Logged in as " + authData.username());
    }

    private static void logoutUser() throws IOException {
        facade.logout(authToken);
        authToken = null;
        System.out.println("Logged out successfully.");
    }

    private static void createGame() throws IOException {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        var game = facade.createGame(authToken, gameName);
        System.out.println("Game created with ID: " + game.gameID());
    }

    private static void listGames() throws IOException {
        List<GameData> games = facade.listGames(authToken);
        gameList = new HashMap<>();
        int index = 1;

        for (GameData game : games) {
            gameList.put(index, game);
            System.out.printf("%d: %s (ID: %d)%n", index++, game.gameName(), game.gameID());
        }
    }

    private static void playGame() throws IOException {
        if (gameList == null || gameList.isEmpty()) {
            System.out.println("No games available. Please list games first.");
            return;
        }

        System.out.print("Select a game number: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());
        GameData game = gameList.get(gameNumber);

        System.out.print("Choose your color (WHITE/BLACK): ");
        String color = scanner.nextLine().toUpperCase();

        facade.joinGame(authToken, color, game.gameID());
        System.out.println("Joined game " + game.gameName() + " as " + color);
    }

    private static void clearDatabase() throws IOException {
        facade.clearDatabase();
        System.out.println("Database cleared.");
    }

    private static void quit() {
        System.out.println("Goodbye!");
        running = false;
    }
}
