package service;

import dataaccess.DBMemoryAccess;
import datamodel.AuthData;
import datamodel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.ResponseException;
import server.Server;

import java.util.List;

class GameServiceTest {

    @Test
    void createGameSuccess() throws ResponseException {

        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
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

        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        Assertions.assertThrows(ResponseException.class, () -> gameService.createGame("cow", "name"));
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int gameId = gameService.createGame(res.authToken(), "name");
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, gameId));
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(res.authToken(), Server.PlayerColor.BLACK, gameId));

    }

    @Test
    void joinGameFail() throws ResponseException {
        {
            DataAccess dataAccess = new DBMemoryAccess();
            dataAccess.deleteDatabase();
            GameService gameService = new GameService(dataAccess);
            UserService userService = new UserService(dataAccess);
            UserData userData = new UserData("Joe", "password", "joe@joe");
            userService.register(userData);
            AuthData res = userService.login(userData);
            int x = gameService.createGame(res.authToken(), "name");
            Assertions.assertDoesNotThrow(() -> gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x));
            Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x));

        }
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        List<GameData> gameList = gameService.listGames(res.authToken());
        Assertions.assertNotNull(gameList);
    }


    @Test
    void listGamesFail() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        Assertions.assertThrows(ResponseException.class, () -> gameService.listGames("cow"));
    }
}
