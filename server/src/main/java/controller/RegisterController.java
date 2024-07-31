package controller;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterController {

    public Object handleRegister(Request req, Response res) {
        UserService userService = new UserService();
        UserData registrationRequest = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authResponse = userService.registerUser(registrationRequest);
            res.status(200);
            return new Gson().toJson(authResponse);
        } catch (DataAccessException e) {
            res.status(400);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
