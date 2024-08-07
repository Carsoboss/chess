package serverfacade;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

        handleRequest(connection, null, Void.class);
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

        return handleRequest(connection, null, GameData[].class);
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws IOException {
        String requestType = "PUT";
        String route = "/game";
        HttpURLConnection connection = getHTTPConnection(requestType, route, authToken);

        GameData joinInfo = new GameData(gameID,
                "WHITE".equals(playerColor) ? authToken : null,
                "BLACK".equals(playerColor) ? authToken : null,
                null, null);

        handleRequest(connection, joinInfo, Void.class);
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
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                return gson.fromJson(reader, responseClass);
            }
        } else {
            throw new IOException("HTTP request failed with status " + responseCode);
        }
    }
}
