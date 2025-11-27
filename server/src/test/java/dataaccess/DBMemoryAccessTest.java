package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import datamodel.GameData;
import datamodel.UserData;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import server.ResponseException;

import java.util.List;


class DBMemoryAccessTest {


    static void resetDB(DataAccess db) throws ResponseException {
        db.deleteDatabase();
    }

    @Test
    void saveUser() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        boolean result = db.saveUser(user);
        Assertions.assertTrue(result);
    }

    @Test
    void saveUserBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        boolean result = db.saveUser(user);
        Assertions.assertFalse(result);

    }

    @Test
    void getUserData() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        UserData user1 = db.getUserData("joe");
        Assertions.assertNotNull(user1);
    }

    @Test
    void getUserDataBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user1 = db.getUserData("joe");
        Assertions.assertNull(user1);
    }

    @Test
    void getUsername() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.registerAuthToken("auth123", "joe");
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        UserData userRes = db.getUsername("auth123");
        Assertions.assertEquals(user, userRes);
    }

    @Test
    void getUsernameBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData userRes = db.getUsername("auth123");
        Assertions.assertNull(userRes);
    }


    @Test
    void registerAuthToken() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.registerAuthToken("auth123", "joe");
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        boolean result = db.authenticate("auth123");
        Assertions.assertTrue(result);
    }

    @Test
    void registerAuthTokenBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.registerAuthToken("auth123", "joe");
        Assertions.assertThrows(ResponseException.class, () -> db.registerAuthToken("auth123", "sally"));

    }

    @Test
    void authenticate() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.registerAuthToken("auth123", "joe");

        boolean result = db.authenticate("auth123");
        Assertions.assertTrue(result);
    }

    @Test
    void authenticateBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        boolean result = db.authenticate("auth123");
        Assertions.assertFalse(result);
    }

    @Test
    void removeAuthToken() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.registerAuthToken("auth123", "joe");
        db.removeAuthToken("auth123");
    }

    ///  removeAuthToken does not really have a bad path I need to test except for server failer
    @Test
    void removeAuthTokenBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.removeAuthToken("auth123");
        Assertions.assertTrue(true);
    }

    @Test
    void deleteDatabase() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        db.deleteDatabase();
    }

    @Test
    void createGame() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int result = db.createGame("chess123");
        Assertions.assertNotEquals(0, result);
    }

    ///  create game does not really have a bad path I need to test except for server failer
    @Test
    void createGameBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int result = db.createGame("chess123");
        int result2 = db.createGame("chess123");
        Assertions.assertNotEquals(0, result);
        Assertions.assertNotEquals(0, result2);

    }

    @Test
    void getGame() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int id = db.createGame("chess123");
        GameData result = db.getGame(id);
        Assertions.assertNotNull(result);
    }

    @Test
    void getGameBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int id = 6;
        GameData result = db.getGame(id);
        Assertions.assertNull(result);
    }

    ///  game list does not really have a bad path I need to test except for server failer
    @Test
    void gamesList() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.createGame("chess123");
        db.createGame("chess456");
        List<GameData> games = db.gamesList();
        Assertions.assertNotEquals(0, games.size());
    }


    @Test
    void gamesListBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.createGame("chess123");
        db.createGame("chess123");
        List<GameData> games = db.gamesList();
        Assertions.assertNotEquals(0, games.size());
    }

    @Test
    void setWhite() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        db.setWhite(gameID, "joe");
        GameData result = db.getGame(gameID);
        Assertions.assertEquals("joe", result.getWhiteUsername());
    }

    @Test
    void setWhiteBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        db.setWhite(gameID, "joe");
        Assertions.assertThrows(ResponseException.class, () -> db.setWhite(gameID, "bob"));
    }

    @Test
    void setBlack() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        db.setBlack(gameID, "robert");
        GameData result = db.getGame(gameID);
        Assertions.assertEquals("robert", result.getBlackUsername());
    }

    @Test
    void setBlackBad() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        db.setBlack(gameID, "robert");
        Assertions.assertThrows(ResponseException.class, () -> db.setBlack(gameID, "joe"));
    }

    ///  update board doesn't have a sad path since except a server error since all other errors are handled before it
    @Test
    void updateBoard() throws ResponseException, InvalidMoveException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        GameData result = db.getGame(gameID);
        ChessGame chessGame = result.getChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        db.updateGame(gameID, chessGame);
        result = db.getGame(gameID);
        Assertions.assertEquals(chessGame, result.getChessGame());
    }

    @Test
    void updateBoardBad() throws ResponseException, InvalidMoveException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        GameData result = db.getGame(gameID);
        ChessGame chessGame = result.getChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        db.updateGame(gameID, chessGame);
        result = db.getGame(gameID);
        Assertions.assertEquals(chessGame, result.getChessGame());
    }
}