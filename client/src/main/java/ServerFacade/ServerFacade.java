package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import controller.CreateGameRequest;
import controller.JoinGameRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String baseUrl;

    public ServerFacade(String host, int port) {
        this.baseUrl = String.format("http://%s:%d", host, port);
    }

    public AuthData register(String username, String password, String email) throws IOException {
        String url = baseUrl + "/user";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("email", email);

        return sendPostRequest(url, requestBody, AuthData.class);
    }

    public AuthData login(String username, String password) throws IOException {
        String url = baseUrl + "/session";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        return sendPostRequest(url, requestBody, AuthData.class);
    }

    public void logout(String authToken) throws IOException {
        String url = baseUrl + "/session";
        sendDeleteRequest(url, authToken);
    }

    public List<GameData> listGames(String authToken) throws IOException {
        String url = baseUrl + "/game";
        return sendGetRequest(url, authToken, List.class);
    }

    public GameData createGame(String authToken, String gameName) throws IOException {
        String url = baseUrl + "/game";
        CreateGameRequest requestBody = new CreateGameRequest(gameName);

        return sendPostRequest(url, authToken, requestBody, GameData.class);
    }

    public GameData joinGame(String authToken, String playerColor, int gameID) throws IOException {
        String url = baseUrl + "/game";
        JoinGameRequest requestBody = new JoinGameRequest(playerColor, gameID);

        return sendPutRequest(url, authToken, requestBody, GameData.class);
    }

    public void clearDatabase() throws IOException {
        String url = baseUrl + "/db";
        sendDeleteRequest(url, null);
    }

    private <T> T sendPostRequest(String url, Map<String, ?> body, Class<T> responseType) throws IOException {
        return sendRequest(url, "POST", null, body, responseType);
    }

    private <T> T sendPostRequest(String url, String authToken, Map<String, ?> body, Class<T> responseType) throws IOException {
        return sendRequest(url, "POST", authToken, body, responseType);
    }

    private <T> T sendPutRequest(String url, String authToken, Map<String, ?> body, Class<T> responseType) throws IOException {
        return sendRequest(url, "PUT", authToken, body, responseType);
    }

    private <T> T sendGetRequest(String url, String authToken, Class<T> responseType) throws IOException {
        return sendRequest(url, "GET", authToken, null, responseType);
    }

    private void sendDeleteRequest(String url, String authToken) throws IOException {
        sendRequest(url, "DELETE", authToken, null, Void.class);
    }

    private <T> T sendRequest(String url, String method, String authToken, Map<String, ?> body, Class<T> responseType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        if (body != null) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = new Gson().toJson(body).getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return new Gson().fromJson(new InputStreamReader(connection.getInputStream(), "utf-8"), responseType);
        } else {
            throw new IOException("HTTP error code: " + connection.getResponseCode());
        }
    }
}
