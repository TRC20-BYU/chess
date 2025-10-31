package service;

import dataaccess.DBMemoryAccess;
import datamodel.AuthData;
import datamodel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.ResponseException;

class UserServiceTest {


    @Test
    void delete() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        userService.deleteDatabase();
        var result = userService.login(userData);
        Assertions.assertNull(result);
    }

    @Test
    void registerSuccess() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        AuthData res = userService.register(userData);
        Assertions.assertNotNull(res);
    }

    @Test
    void registerFail() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.register(userData);
        Assertions.assertNull(res);
    }

    @Test
    void loginSuccess() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        var res = userService.login(userData);
        Assertions.assertNotNull(res);
    }

    @Test
    void loginFail() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        var res = userService.login(userData);
        Assertions.assertNull(res);
    }

    @Test
    void logoutSuccess() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        boolean passed = userService.logout(res.authToken());
        Assertions.assertTrue(passed);
    }

    @Test
    void logoutFail() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        UserService userService = new UserService(dataAccess);
        boolean passed = userService.logout("cow");
        Assertions.assertFalse(passed);
    }
}