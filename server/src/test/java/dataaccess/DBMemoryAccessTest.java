package dataaccess;

import datamodel.UserData;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;


class DBMemoryAccessTest {


    static void resetDB(DataAccess db) {
        db.deleteDatabase();
    }

    @Test
    void saveUser() {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        boolean result = db.saveUser(user);
        Assertions.assertTrue(result);
    }

    @Test
    void saveUserBad() {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        boolean result = db.saveUser(user);
        Assertions.assertFalse(result);

    }

    @Test
    void getUserData() {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        UserData user1 = db.getUserData("joe");
        Assertions.assertNotNull(user1);
    }

    @Test
    void getUserDataBad() {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user1 = db.getUserData("joe");
        Assertions.assertNull(user1);
    }

    @Test
    void getUsername() {
        DBMemoryAccess db = new DBMemoryAccess();
        resetDB(db);
        UserData user1 = db.getUserData("joe");
        Assertions.assertNull(user1);
    }

    @Test
    void registerAuthToken() {
    }

    @Test
    void authenticate() {
    }

    @Test
    void removeAuthToken() {
    }

    @Test
    void deleteDatabase() {
        DBMemoryAccess db = new DBMemoryAccess();
        db.deleteDatabase();
    }

    @Test
    void createGame() {
    }

    @Test
    void getGame() {
    }

    @Test
    void gamesList() {
    }
}