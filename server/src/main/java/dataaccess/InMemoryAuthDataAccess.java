package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InMemoryAuthDataAccess implements AuthDataAccess {
    private final Set<AuthData> auths = new HashSet<>();

    @Override
    public AuthData createAuth(String username) {
        String authToken;
        do {
            authToken = UUID.randomUUID().toString();
        } while (retrieveAuth(authToken) != null); // Ensure the token is unique
        AuthData authData = new AuthData(authToken, username);
        auths.add(authData);
        return authData;
    }

    @Override
    public AuthData retrieveAuth(String authToken) {
        return auths.stream()
                .filter(auth -> auth.authToken().equals(authToken))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.removeIf(auth -> auth.authToken().equals(authToken));
    }

    @Override
    public void deleteAllAuths() {
        auths.clear();
    }
}
