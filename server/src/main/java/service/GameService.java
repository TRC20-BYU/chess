package service;

import com.google.gson.Gson;
import dataModel.GameData;
import dataaccess.DataAccess;
import server.Server;

import java.util.Map;

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

    public int joinGame(String authToken, Server.PlayerColor playerColor, int gameId) {
        if (dataAccess.authenticate(authToken)) {
            GameData game = dataAccess.getGame(gameId);
            if (game != null) {
                return addColorToGame(authToken, playerColor, game);
            }
        }
        return -1;
    }

    private int addColorToGame(String authToken, Server.PlayerColor playerColor, GameData game) {
        if (playerColor == Server.PlayerColor.WHITE) {
            if (game.whiteUsername() == null) {
                game = new GameData(game.gameID(), dataAccess.getUsername(authToken).username(), game.blackUsername(), game.gameName());
                return 1;
            }
        } else {
            if (game.blackUsername() == null) {
                game = new GameData(game.gameID(), game.whiteUsername(), dataAccess.getUsername(authToken).username(), game.gameName());
                return 1;
            }
        }
        return -2;
    }

    public String listGames(String authToken) {
        if (dataAccess.authenticate(authToken)) {
            return dataAccess.gamesList().toString();
        }
        return null;
    }
}
