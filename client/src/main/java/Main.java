import serverfacade.ServerFacade;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static boolean finished;
    private static AppState state = AppState.PRELOGIN;
    private static ServerFacade facade;
    private static Scanner scanner;
    private static String authToken;
    private static HashMap<Integer, GameData> currGameList;

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

            if (Arrays.asList(getUserOptions(state)).contains(userOption.toLowerCase())) {
                switch (userOption.toLowerCase()) {  // Convert command to lowercase
                    case "help" -> handleHelp();
                    case "quit" -> handleQuit();
                    case "login" -> handleLogin();
                    case "register" -> handleRegister();
                    case "logout" -> handleLogout();
                    case "create" -> handleCreateGame();
                    case "list" -> handleListGames();
                    case "play" -> handlePlayGame();
                    case "observe" -> handleObserveGame();
                    default -> handleUnrecognizedOption();
                }
            } else {
                handleUnrecognizedOption();
            }
        }
    }

    private static void handleHelp() {
        String preLoginHelp = """
                ----------------------------------------
                Available commands:
                register - register new user
                login - log in existing user
                quit - exit chess app
                help - display this help text
                ----------------------------------------
                """;

        String postLoginHelp = """
                ----------------------------------------
                Available commands:
                logout - log out current user
                create - create new game
                list - list all current games
                play - play chess
                observe - observe chess game without joining
                help - display this help text
                quit - exit chess app
                ----------------------------------------
                """;

        switch (state) {
            case PRELOGIN -> System.out.print(preLoginHelp);
            case POSTLOGIN -> System.out.print(postLoginHelp);
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
            state = AppState.POSTLOGIN;

            System.out.println("Successfully logged in as " + username + "\n");
        } catch (IOException e) {
            System.out.println("Login failed: " + e.getMessage() + "\n");
        }
        handleHelp();
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
            state = AppState.POSTLOGIN;

            System.out.println("Successfully registered and logged in as " + username + "\n");
        } catch (IOException e) {
            System.out.println("Registration failed: " + e.getMessage() + "\n");
        }
        handleHelp();
    }

    private static void handleLogout() {
        try {
            facade.logout(authToken);
            authToken = null;
            currGameList = null;
            state = AppState.PRELOGIN;
            System.out.println("Successfully logged out.\n");
        } catch (IOException e) {
            System.out.println("Logout failed: " + e.getMessage() + "\n");
        }
        handleHelp();
    }

    private static void handleCreateGame() {
        if (authToken == null) {
            System.out.println("You must be logged in to create a game.\n");
            return;
        }

        try {
            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine();

            facade.createGame(gameName, authToken);
            System.out.println("\nGame '" + gameName + "' created successfully.\n");
        } catch (IOException e) {
            System.out.println("Failed to create game: " + e.getMessage() + "\n");
        }
    }

    private static void handleListGames() {
        if (authToken == null) {
            System.out.println("You must be logged in to list games.\n");
            return;
        }

        try {
            currGameList = new HashMap<>();
            GameData[] games = facade.listGames(authToken);
            if (games.length == 0) {
                System.out.println("\nNo available games.\n");
            } else {
                System.out.println("\nAvailable games:");
                for (int i = 0; i < games.length; i++) {
                    currGameList.put(i, games[i]);
                    System.out.println((i + 1) + ". " + games[i].gameName());
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Failed to list games: " + e.getMessage() + "\n");
        }
    }

    private static void handlePlayGame() {
        if (authToken == null) {
            System.out.println("You must be logged in to play a game.\n");
            return;
        }

        if (currGameList == null || currGameList.isEmpty()) {
            System.out.println("No games available to join. Please list games first.\n");
            return;
        }

        try {
            System.out.print("Enter game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter player color (WHITE or BLACK): ");
            String playerColor = scanner.nextLine().toUpperCase();  // Convert input to uppercase

            GameData game = currGameList.get(gameNumber - 1);
            facade.joinGame(game.gameID(), playerColor, authToken);
            System.out.println("Successfully joined the game: " + game.gameName() + "\n");

            // Here, you would call your method to draw the initial chessboard
            // drawStartingBoard();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to join game: " + e.getMessage() + "\n");
        }
    }

    private static void handleObserveGame() {
        if (authToken == null) {
            System.out.println("You must be logged in to observe a game.\n");
            return;
        }

        if (currGameList == null || currGameList.isEmpty()) {
            System.out.println("No games available to observe. Please list games first.\n");
            return;
        }

        try {
            System.out.print("Enter game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            GameData game = currGameList.get(gameNumber - 1);
            System.out.println("Observing the game: " + game.gameName() + "\n");

            // Here, you would call your method to draw the initial chessboard
            // drawStartingBoard();
        } catch (NumberFormatException e) {
            System.out.println("Failed to observe game: " + e.getMessage() + "\n");
        }
    }

    private static void handleUnrecognizedOption() {
        System.out.println("Command not recognized. Type 'help' for a list of commands.\n");
    }

    private enum AppState {
        PRELOGIN,
        POSTLOGIN
    }

    private static String[] getUserOptions(AppState state) {
        return switch (state) {
            case PRELOGIN -> new String[]{"help", "quit", "login", "register"};
            case POSTLOGIN -> new String[]{"help", "logout", "create", "list", "play", "observe", "quit"};
        };
    }
}
