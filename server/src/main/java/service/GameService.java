package service;

import datamodel.GameData;
import dataaccess.DataAccess;
import server.ResponseException;
import server.Server;

import java.util.List;

public class GameService {

    private final DataAccess dataAccess;


    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            return dataAccess.createGame(gameName);
        }
        throw new ResponseException(ResponseException.Code.authError);
    }

    public int joinGame(String authToken, Server.PlayerColor playerColor, int gameId) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            String username = dataAccess.getUsername(authToken).username();
            if (playerColor == Server.PlayerColor.WHITE) {
                dataAccess.setWhite(gameId, username);
            } else {
                dataAccess.setBlack(gameId, username);
            }
        }
        throw new ResponseException(ResponseException.Code.authError);
    }

    private void addColorToGame(String authToken, Server.PlayerColor playerColor, GameData game) throws ResponseException {
        if (playerColor == Server.PlayerColor.WHITE) {
            if (game.getWhiteUsername() == null) {
                game.setWhiteUsername(dataAccess.getUsername(authToken).username());
            }
        } else {
            if (game.getBlackUsername() == null) {
                game.setBlackUsername(dataAccess.getUsername(authToken).username());
            }
        }
        throw new ResponseException(ResponseException.Code.takenError);
    }

    public List<GameData> listGames(String authToken) {
        if (dataAccess.authenticate(authToken)) {
            return dataAccess.gamesList();
        }
        return null;
    }
}
