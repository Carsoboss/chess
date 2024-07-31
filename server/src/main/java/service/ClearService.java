package service;

import requestresult.ClearResponse;
import service.ServiceException;


public interface IClearService {
    ClearResponse clearAll() throws ServiceException;
}
