package serverfacade;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.AuthData;
import model.GameData;
import controller.CreateGameRequest;
import controller.JoinGameRequest;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {

    private final String serverHost;
    private final int serverPort;

    public ServerFacade(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public AuthData register(String username, String password, String email) throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/user";
        UserData userData = new UserData(username, password, email);
        return sendPostRequest(url, null, userData, AuthData.class);
    }

    public AuthData login(String username, String password) throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/session";
        UserData userData = new UserData(username, password, null);
        return sendPostRequest(url, null, userData, AuthData.class);
    }

    public void logout(String authToken) throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/session";
        sendDeleteRequest(url, authToken);
    }

    public GameData createGame(String authToken, String gameName) throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/game";
        CreateGameRequest requestBody = new CreateGameRequest(gameName);
        return sendPostRequest(url, authToken, requestBody, GameData.class);
    }

    public GameData joinGame(String authToken, String playerColor, int gameId) throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/game";
        JoinGameRequest requestBody = new JoinGameRequest(playerColor, gameId);
        return sendPutRequest(url, authToken, requestBody, GameData.class);
    }

    public GameData[] listGames(String authToken) throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/game";
        return sendGetRequest(url, authToken, GameData[].class);
    }

    public void clearDatabase() throws IOException {
        String url = "http://" + serverHost + ":" + serverPort + "/db";
        sendDeleteRequest(url, null);
    }

    private <T, R> T sendPostRequest(String urlString, String authToken, R requestBody, Class<T> responseType) throws IOException {
        HttpURLConnection connection = createConnection(urlString, "POST", authToken);
        return sendRequest(connection, requestBody, responseType);
    }

    private <T, R> T sendPutRequest(String urlString, String authToken, R requestBody, Class<T> responseType) throws IOException {
        HttpURLConnection connection = createConnection(urlString, "PUT", authToken);
        return sendRequest(connection, requestBody, responseType);
    }

    private <T> T sendGetRequest(String urlString, String authToken, Class<T> responseType) throws IOException {
        HttpURLConnection connection = createConnection(urlString, "GET", authToken);
        return getResponse(connection, responseType);
    }

    private void sendDeleteRequest(String urlString, String authToken) throws IOException {
        HttpURLConnection connection = createConnection(urlString, "DELETE", authToken);
        connection.getResponseCode(); // Force the connection to send the request
    }

    private <T, R> T sendRequest(HttpURLConnection connection, R requestBody, Class<T> responseType) throws IOException {
        if (requestBody != null) {
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                new Gson().toJson(requestBody, writer);
                writer.flush();
            }
        }
        return getResponse(connection, responseType);
    }

    private <T> T getResponse(HttpURLConnection connection, Class<T> responseType) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                connection.getResponseCode() == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream(),
                StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, responseType);
        } catch (JsonSyntaxException e) {
            return null; // Return null if the response doesn't match the expected type
        }
    }

    private HttpURLConnection createConnection(String urlString, String method, String authToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }
        return connection;
    }
}

