package service;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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

    public void joinGame(String authToken, Server.PlayerColor playerColor, int gameId) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            String username = dataAccess.getUsername(authToken).username();
            if (playerColor == Server.PlayerColor.WHITE) {
                dataAccess.setWhite(gameId, username);
            } else {
                dataAccess.setBlack(gameId, username);
            }
        } else {
            throw new ResponseException(ResponseException.Code.authError);
        }
    }


    public List<GameData> listGames(String authToken) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            return dataAccess.gamesList();
        }
        throw new ResponseException(ResponseException.Code.authError);
    }

    public ChessGame makeMove(int gameId, ChessMove chessMove) throws ResponseException {
        ChessGame chessGame = dataAccess.getGame(gameId).getChessGame();
        try {
            chessGame.makeMove(chessMove);
            dataAccess.updateGame(gameId, chessGame);
        } catch (InvalidMoveException e) {

        }
        return chessGame;
    }
}
