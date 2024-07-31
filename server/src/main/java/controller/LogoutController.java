package controller;

import dataaccess.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutController {

    public Object handleLogout(Request req, Response res) {
        UserService userService = new UserService();
        String authToken = req.headers("Authorization");

        try {
            userService.logoutUser(authToken);
            res.status(200);
            return "{ \"success\": true }";
        } catch (DataAccessException e) {
            res.status(400);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
