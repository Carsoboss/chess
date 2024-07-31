package controller;

import dataaccess.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutController {

    private final UserService userService;

    public LogoutController(UserService userService) {
        this.userService = userService;
    }

    public Object handleLogout(Request req, Response res) {
        String authToken = req.headers("Authorization");

        try {
            userService.logoutUser(authToken);
            res.status(200);
            return "{ \"success\": true }";
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
