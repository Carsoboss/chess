package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    public Object handleRegister(Request req, Response res) {
        UserData registrationRequest = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authResponse = userService.registerUser(registrationRequest);
            res.status(200);
            return new Gson().toJson(authResponse);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken")) {
                res.status(403); // Forbidden
            } else {
                res.status(400); // Bad request
            }
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}


