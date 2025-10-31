package service;

import datamodel.GameData;
import datamodel.AuthData;
import datamodel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.ResponseException;
import server.Server;

import java.util.List;

class GameServiceTest {

    @Test
    void createGameSuccess() throws ResponseException {

        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        Assertions.assertTrue(x > 0);
    }

    @Test
    void createGameFail() throws ResponseException {

        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        int x = gameService.createGame("cow", "name");
        Assertions.assertFalse(x > 0);
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int gameId = gameService.createGame(res.authToken(), "name");
        int result = gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, gameId);
        Assertions.assertTrue(result > 0);
        result = gameService.joinGame(res.authToken(), Server.PlayerColor.BLACK, gameId);
        Assertions.assertTrue(result > 0);

    }

    @Test
    void joinGameFail() throws ResponseException {
        {
            DataAccess dataAccess = new MemoryDataAccess();
            GameService gameService = new GameService(dataAccess);
            UserService userService = new UserService(dataAccess);
            UserData userData = new UserData("Joe", "password", "joe@joe");
            userService.register(userData);
            AuthData res = userService.login(userData);
            int x = gameService.createGame(res.authToken(), "name");
            int y = gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
            Assertions.assertTrue(y > 0);
            y = gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
            Assertions.assertFalse(y > 0);

        }
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        List<GameData> gameList = gameService.listGames(res.authToken());
        Assertions.assertNotNull(gameList);
    }


    @Test
    void listGamesFail() {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        List<GameData> gameList = gameService.listGames("cow");
        Assertions.assertNull(gameList);
    }
}