package controller;

import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearDatabaseController {

    public Object handleClearDatabase(Request req, Response res) {
        ClearService clearService = new ClearService();
        clearService.clearDatabase();
        res.status(200);
        return "{ \"success\": true }";
    }
}
