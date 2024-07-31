package service;

import requestresult.ClearResponse;
import service.ServiceException;

import dataaccess.DAOFactory;

public class MemoryClearService implements IClearService {
    private final DAOFactory daoFactory;

    public MemoryClearService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public ClearResponse clearAll() throws ServiceException {
        try {
            daoFactory.getUserDAO().deleteAllUsers();
            daoFactory.getAuthDAO().removeAllAuthTokens();
            daoFactory.getGameDAO().deleteAllGames();
            return new ClearResponse();
        } catch (Exception e) {
            throw new ServiceException("Error clearing data", e);
        }
    }
}
