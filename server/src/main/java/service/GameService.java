package service;

import dataaccess.DataAccess;

public class GameService {

    private final DataAccess dataAccess;


    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameName) {
        if (dataAccess.authenticate(authToken)) {
            return dataAccess.createGame(gameName);
        }
        return -1;
    }
}
