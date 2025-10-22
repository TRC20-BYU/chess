package service;

import datamodel.GameData;
import dataaccess.DataAccess;
import server.Server;

import java.util.List;

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
            if (game.getWhiteUsername() == null) {
                game.setWhiteUsername(dataAccess.getUsername(authToken).username());
                return 1;
            }
        } else {
            if (game.getBlackUsername() == null) {
                game.setBlackUsername(dataAccess.getUsername(authToken).username());
                return 1;
            }
        }
        return -2;
    }

    public List<GameData> listGames(String authToken) {
        if (dataAccess.authenticate(authToken)) {
            return dataAccess.gamesList();
        }
        return null;
    }
}
