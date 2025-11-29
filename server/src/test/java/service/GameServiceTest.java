package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DBMemoryAccess;
import datamodel.AuthData;
import datamodel.UserData;
import dataaccess.DataAccess;
import datamodel.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.ResponseException;
import server.Server;

import java.net.SocketAddress;
import java.time.Duration;
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

    @Test
    void makeMoveGood() throws ResponseException, InvalidMoveException {
        ChessMove chessMove = new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null);
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        ChessGame result = gameService.makeMove(res.authToken(), x, chessMove);
        ChessGame chessGame = new ChessGame();
        chessGame.makeMove(chessMove);
        Assertions.assertEquals(result, chessGame);
    }

    @Test
    void connectService() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        Session session = generateSession();
        gameService.connectService(res.authToken(), x, session);
        Assertions.assertEquals(gameService.getConnects(x).getWhitePlayer(), session);
    }

    @Test
    void connectServiceBad() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        Session session = generateSession();
        Assertions.assertThrows(ResponseException.class, () -> gameService.connectService("bad", 1, session));
    }

    @Test
    void disconnectService() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        Session session = generateSession();
        gameService.connectService(res.authToken(), x, session);
        gameService.disconnectService(res.authToken(), x, session);
        Assertions.assertNull(gameService.getConnects(x).getWhitePlayer());
    }

    @Test
    void disconnectServiceBad() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        Session session = generateSession();
        gameService.connectService(res.authToken(), x, session);
        Assertions.assertThrows(ResponseException.class, () -> gameService.disconnectService("bad", x, session));

    }

    @Test
    void getConnections() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        UserData userData = new UserData("Joe", "password", "joe@joe");
        userService.register(userData);
        AuthData res = userService.login(userData);
        int x = gameService.createGame(res.authToken(), "name");
        gameService.joinGame(res.authToken(), Server.PlayerColor.WHITE, x);
        Session session = generateSession();
        gameService.connectService(res.authToken(), x, session);
        Assertions.assertEquals(gameService.getConnects(x).getWhitePlayer(), session);
    }

    @Test
    void getConnectionsBad() throws ResponseException {
        DataAccess dataAccess = new DBMemoryAccess();
        dataAccess.deleteDatabase();
        GameService gameService = new GameService(dataAccess);
        Assertions.assertNull(gameService.getConnects(3));
    }

    Session generateSession() {
        return new Session() {
            @Override
            public void close() {

            }

            @Override
            public void close(CloseStatus closeStatus) {

            }

            @Override
            public void close(int i, String s) {

            }

            @Override
            public void disconnect() {

            }

            @Override
            public SocketAddress getLocalAddress() {
                return null;
            }

            @Override
            public String getProtocolVersion() {
                return "";
            }

            @Override
            public RemoteEndpoint getRemote() {
                return null;
            }

            @Override
            public SocketAddress getRemoteAddress() {
                return null;
            }

            @Override
            public UpgradeRequest getUpgradeRequest() {
                return null;
            }

            @Override
            public UpgradeResponse getUpgradeResponse() {
                return null;
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public SuspendToken suspend() {
                return null;
            }

            @Override
            public WebSocketBehavior getBehavior() {
                return null;
            }

            @Override
            public Duration getIdleTimeout() {
                return null;
            }

            @Override
            public int getInputBufferSize() {
                return 0;
            }

            @Override
            public int getOutputBufferSize() {
                return 0;
            }

            @Override
            public long getMaxBinaryMessageSize() {
                return 0;
            }

            @Override
            public long getMaxTextMessageSize() {
                return 0;
            }

            @Override
            public long getMaxFrameSize() {
                return 0;
            }

            @Override
            public boolean isAutoFragment() {
                return false;
            }

            @Override
            public void setIdleTimeout(Duration duration) {

            }

            @Override
            public void setInputBufferSize(int i) {

            }

            @Override
            public void setOutputBufferSize(int i) {

            }

            @Override
            public void setMaxBinaryMessageSize(long l) {

            }

            @Override
            public void setMaxTextMessageSize(long l) {

            }

            @Override
            public void setMaxFrameSize(long l) {

            }

            @Override
            public void setAutoFragment(boolean b) {

            }
        };
    }
}
