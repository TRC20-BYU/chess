package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import server.ResponseException;

import java.util.List;


class DBMemoryAccessTest {


    static void resetDB(DataAccess db) {
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
        UserData user1 = db.getUserData("joe");
        Assertions.assertNull(user1);
    }

    @Test
    void registerAuthToken() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        db.registerAuthToken("auth123", "joe");
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
    void setWhite() throws ResponseException {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        int gameID = db.createGame("chess123");
        db.setWhite(gameID, "joe");
        GameData result = db.getGame(gameID);
        Assertions.assertEquals("joe", result.getWhiteUsername());
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
}