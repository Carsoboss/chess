package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MemoryAuthDAO implements IAuthDAO {
    private final Set<AuthData> authTokens = new HashSet<>();

    @Override
    public AuthData createAuthToken(String username) {
        AuthData authToken;
        do {
            authToken = new AuthData(UUID.randomUUID().toString(), username);
        } while (authTokens.contains(authToken));

        authTokens.add(authToken);
        return authToken;
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DAOException {
        return authTokens.stream()
                .filter(auth -> auth.getAuthToken().equals(authToken))
                .findFirst()
                .orElseThrow(() -> new DAOException("Auth token not found"));
    }

    @Override
    public void removeAuthToken(String authToken) throws DAOException {
        if (!authTokens.removeIf(auth -> auth.getAuthToken().equals(authToken))) {
            throw new DAOException("Auth token not found");
        }
    }

    @Override
    public void removeAllAuthTokens() {
        authTokens.clear();
    }

    @Override
    public int getAuthTokenCount() {
        return authTokens.size();
    }
}