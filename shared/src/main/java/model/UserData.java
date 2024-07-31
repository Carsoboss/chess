package model;

public record UserData(String username, String password, String email) {
    // Corrected getters
    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String email() {
        return email;
    }
}
