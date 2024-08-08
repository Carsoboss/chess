package serverfacade;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverURL;
    private final Gson gson;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        this.gson = new Gson();
    }

    public AuthData register(String username, String password, String email) throws IOException {
        String requestType = "POST";
        String route = "/user";
        HttpURLConnection connection = getHTTPConnection(requestType, route, null);

        UserData user = new UserData(username, password, email);
        return handleRequest(connection, user, AuthData.class);
    }

    public AuthData login(String username, String password) throws IOException {
        String requestType = "POST";
        String route = "/session";
        HttpURLConnection connection = getHTTPConnection(requestType, route, null);

        UserData user = new UserData(username, password, null);
        return handleRequest(connection, user, AuthData.class);
    }

    public void logout(String authToken) throws IOException {
        String requestType = "DELETE";
        String route = "/session";
        HttpURLConnection connection = getHTTPConnection(requestType, route, authToken);

        handleRequest(connection, null, Map.class);  // Expecting a success message from server
    }

    public GameData createGame(String gameName, String authToken) throws IOException {
        String requestType = "POST";
        String route = "/game";
        HttpURLConnection connection = getHTTPConnection(requestType, route, authToken);

        GameData newGame = new GameData(0, null, null, gameName, null);
        return handleRequest(connection, newGame, GameData.class);
    }

    public GameData[] listGames(String authToken) throws IOException {
        String requestType = "GET";
        String route = "/game";
        HttpURLConnection connection = getHTTPConnection(requestType, route, authToken);

        // Reading and printing the raw response for debugging
        InputStream responseBody = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
        StringBuilder responseString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }
        System.out.println("Raw Server Response: " + responseString);

        try {
            // Parse the response as a JsonObject
            JsonObject responseObject = gson.fromJson(responseString.toString(), JsonObject.class);

            // Extract the "games" array from the response object
            GameData[] games = gson.fromJson(responseObject.get("games"), GameData[].class);
            System.out.println("Successfully listed games.");
            return games;
        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse response as array: " + e.getMessage());
            throw new IOException("Failed to parse server response.", e);
        }
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws IOException {
        String requestType = "PUT";
        String route = "/game";
        HttpURLConnection connection = getHTTPConnection(requestType, route, authToken);

        // Convert player color to uppercase to ensure consistency
        playerColor = playerColor.toUpperCase();
        System.out.println("Debug: Preparing to send color " + playerColor + " in the join game request.");

        GameData joinInfo = new GameData(gameID,
                "WHITE".equals(playerColor) ? authToken : null,
                "BLACK".equals(playerColor) ? authToken : null,
                null, null);

        System.out.println("Debug: GameData prepared with GameID: " + gameID + ", PlayerColor: " + playerColor);

        handleRequest(connection, joinInfo, Map.class);  // Expecting a success message from server
    }


    public Map<String, Object> clear() throws IOException {
        String requestType = "DELETE";
        String route = "/db";
        HttpURLConnection connection = getHTTPConnection(requestType, route, null);

        return handleRequest(connection, null, HashMap.class);
    }

    private HttpURLConnection getHTTPConnection(String httpType, String route, String authToken) throws IOException {
        String urlString = serverURL + route;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(httpType);
        connection.setDoOutput(true);

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        return connection;
    }

    private <T> T handleRequest(HttpURLConnection connection, Object request, Class<T> responseClass) throws IOException {
        if (request != null) {
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream requestBody = connection.getOutputStream()) {
                String jsonBody = gson.toJson(request);
                requestBody.write(jsonBody.getBytes());
            }
        }

        int responseCode = connection.getResponseCode();
        InputStream responseBody = responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
        StringBuilder responseString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }

        System.out.println("Server Response: " + responseString);

        T response;
        try {
            response = gson.fromJson(responseString.toString(), responseClass);
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new IOException("Failed to parse server response.", e);
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP request failed with status " + responseCode);
        }

        return response;
    }
}