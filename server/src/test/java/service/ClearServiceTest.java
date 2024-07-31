package service;

import model.UserData;
import org.junit.jupiter.api.Test;

class ClearServiceTest {

    @Test
    void clearEmptyDB() {

    }

    @Test
    void clearPopulatedDB() {
        UserData newUser = new UserData("newUser1", "byu1234", "newemail@email.com");

    }
}