package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.Test;

import datamodel.AuthData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DBMemoryAccessTest {

    @Test
    void saveUser() {
        DBMemoryAccess db = new DBMemoryAccess();
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
    }

    @Test
    void getUserData() {
        DBMemoryAccess db = new DBMemoryAccess();
        UserData user = new UserData("joe", "supersecret", "joe@joe");
        db.saveUser(user);
        UserData user1 = db.getUserData("joe");
        Assertions.assertNotNull(user1);
    }

    @Test
    void getUsername() {
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