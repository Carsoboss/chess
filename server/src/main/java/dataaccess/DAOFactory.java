package dataaccess;

public class DAOFactory {
    private static final DAOFactory instance = new DAOFactory();

    private final IAuthDAO authDAO;
    private final IGameDAO gameDAO;
    private final IUserDAO userDAO;

    private DAOFactory() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
    }

    public static DAOFactory getInstance() {
        return instance;
    }

    public IAuthDAO getAuthDAO() {
        return authDAO;
    }

    public IGameDAO getGameDAO() {
        return gameDAO;
    }

    public IUserDAO getUserDAO() {
        return userDAO;
    }
}
