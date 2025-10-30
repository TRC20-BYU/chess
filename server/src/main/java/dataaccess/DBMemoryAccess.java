package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.List;

public class DBMemoryAccess implements DataAccess {

    public DBMemoryAccess() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.out.print("here");
        }
//        try (var conn = DatabaseManager.getConnection()) {
//
//        }
    }

    @Override
    public boolean saveUser(UserData user) {
        return false;
    }

    @Override
    public UserData getUserData(String username) {
        return null;
    }

    @Override
    public UserData getUsername(String authToken) {
        return null;
    }

    @Override
    public void registerAuthToken(String authToken, String username) {

    }

    @Override
    public boolean authenticate(String authToken) {
        return false;
    }

    @Override
    public void removeAuthToken(String authToken) {

    }

    @Override
    public void deleteDatabase() {

    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public List<GameData> gamesList() {
        return List.of();
    }
}
