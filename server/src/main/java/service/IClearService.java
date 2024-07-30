package service;

import requestresult.ClearResponse;
import dataaccess.ServiceException;

public interface IClearService {
    ClearResponse clearAll() throws ServiceException;
}
