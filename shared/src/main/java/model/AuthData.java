package model;

public record AuthData(String authToken, String username) {
    // Corrected getters
    public String authToken() {
        return authToken;
    }

    public String username() {
        return username;
    }
}
