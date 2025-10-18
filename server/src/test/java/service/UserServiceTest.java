package service;

import dataModel.RegistrationResult;
import dataModel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    DataAccess dataAccess;
    UserService userService;


    @Test
    void register() {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        RegistrationResult res = userService.register(userData);
        Assertions.assertNotNull(res);
        res = userService.register(userData);
        Assertions.assertNull(res);
    }
}