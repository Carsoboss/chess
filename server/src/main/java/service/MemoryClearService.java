package service;

import requestresult.ClearResponse;
import dataaccess.ServiceException;
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
            daoFactory.getAuthDAO().deleteAllAuthTokens();
            daoFactory.getGameDAO().deleteAllGames();
            return new ClearResponse();
        } catch (Exception e) {
            throw new ServiceException("Error clearing data", e);
        }
    }
}
