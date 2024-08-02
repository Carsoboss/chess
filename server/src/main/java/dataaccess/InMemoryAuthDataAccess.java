package dataaccess;

import model.AuthData;
import java.util.HashSet;
import java.util.Set;

public class InMemoryAuthDataAccess implements AuthDataAccess {
    private final Set<AuthData> auths = new HashSet<>();

    @Override
    public AuthData createAuth(String username, String authToken) {
        AuthData authData = new AuthData(username, authToken);
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
