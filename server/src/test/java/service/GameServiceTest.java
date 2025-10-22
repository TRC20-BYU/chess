package service;

import dataModel.RegistrationResult;
import dataModel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void createGame() {

        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        RegistrationResult res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        Assertions.assertTrue(x > 0);
    }

    @Test
    void createGameFail() {

        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        int x = gameService.createGame("cow", "name");
        Assertions.assertFalse(x > 0);
    }

    @Test
    void joinGame() {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        RegistrationResult res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        int y = gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        Assertions.assertTrue(y > 0);
        y = gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        Assertions.assertFalse(y > 0);

    }
}