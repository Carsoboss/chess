package service;

import service.ServiceException;
import requestresult.ClearResponse;

public class ClearService {
    private final IClearService clearService;

    public ClearService() {
        clearService = ServiceFactory.getInstance().getClearService();
    }

    public ClearResponse clear() throws ServiceException {
        return clearService.clearAll();
    }
}
