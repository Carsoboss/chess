package model;

public record AuthData(String authToken, String username) {
    public String getAuthToken() {
        return "";
    }

    public String getUsername() {
    }
}
