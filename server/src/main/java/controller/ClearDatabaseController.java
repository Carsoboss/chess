package controller;

import dataaccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearDatabaseController {

    private final ClearService clearService;

    public ClearDatabaseController(ClearService clearService) {
        this.clearService = clearService;
    }

    public Object handleClearDatabase(Request req, Response res) throws DataAccessException {
        clearService.clearDatabase();
        res.status(200);
        return "{ \"success\": true }";
    }
}
