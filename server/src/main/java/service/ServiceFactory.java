package service;

import dataaccess.DAOFactory;

public class ServiceFactory {
    private static final ServiceFactory instance = new ServiceFactory();
    private final IClearService clearService;
    private final IGameService gameService;
    private final IUserService userService;

    private ServiceFactory() {
        DAOFactory daoFactory = DAOFactory.getInstance();
        clearService = new MemoryClearService(daoFactory);
        gameService = new MemoryGameService(daoFactory);
        userService = new MemoryUserService(daoFactory);
    }

    public static ServiceFactory getInstance() {
        return instance;
    }

    public IClearService getClearService() {
        return clearService;
    }

    public IGameService getGameService() {
        return gameService;
    }

    public IUserService getUserService() {
        return userService;
    }
}
