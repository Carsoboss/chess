package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginController {

    public Object handleLogin(Request req, Response res) {
        UserService userService = new UserService();
        UserData loginRequest = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authResponse = userService.loginUser(loginRequest);
            res.status(200);
            return new Gson().toJson(authResponse);
        } catch (DataAccessException e) {
            res.status(400);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
