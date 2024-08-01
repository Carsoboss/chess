package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    public Object handleLogin(Request req, Response res) {
        UserData loginRequest = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authResponse = userService.loginUser(loginRequest);
            res.status(200);
            return new Gson().toJson(authResponse);
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}