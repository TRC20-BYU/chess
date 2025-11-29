package service;


import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import datamodel.GameConnections;
import datamodel.GameData;
import dataaccess.DataAccess;
import org.eclipse.jetty.websocket.api.Session;
import server.ResponseException;
import server.Server;
import websocket.ConnectionManager;

import java.util.List;
import java.util.Objects;

public class GameService {

    private final DataAccess dataAccess;
    private final ConnectionManager connectionManager;


    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connectionManager = new ConnectionManager();
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

    public ChessGame makeMove(String authToken, int gameId, ChessMove chessMove) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            datamodel.GameData gameData = dataAccess.getGame(gameId);
            ChessGame chessGame = gameData.chessGame();
            if (validateUserColor(authToken, gameData, chessMove)) {
                try {
                    chessGame.makeMove(chessMove);
                    dataAccess.updateGame(gameId, chessGame);
                } catch (InvalidMoveException e) {

                }
                return chessGame;
            } else {
                throw new ResponseException(ResponseException.Code.authError);
            }
        } else {
            throw new ResponseException(ResponseException.Code.authError);
        }
    }

    public boolean validateUserColor(String authToken, GameData gameData, ChessMove chessMove) throws ResponseException {
        String username = dataAccess.getUsername(authToken).username();
        ChessGame.TeamColor teamTurn = gameData.chessGame().getTeamTurn();
        ChessGame.TeamColor pieceColor = gameData.chessGame().getBoard().getPiece(chessMove.getStartPosition()).getTeamColor();
        if (teamTurn == pieceColor) {
            if (teamTurn == ChessGame.TeamColor.WHITE) {
                return Objects.equals(gameData.whiteUsername(), username);
            } else {
                return Objects.equals(gameData.blackUsername(), username);
            }
        } else {
            throw new ResponseException(ResponseException.Code.authError);
        }
    }

    public void connectService(String authToken, Integer gameID, Session session) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            String username = dataAccess.getUsername(authToken).username();
            GameData gameData = dataAccess.getGame(gameID);
            if (Objects.equals(gameData.whiteUsername(), username)) {
                connectionManager.addWhite(gameID, session);
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                connectionManager.addBlack(gameID, session);
            } else {
                connectionManager.addObserver(gameID, session);
            }
        } else {
            throw new ResponseException(ResponseException.Code.authError);
        }
    }

    public void disconnectService(String authToken, Integer gameID, Session session) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            String username = dataAccess.getUsername(authToken).username();
            GameData gameData = dataAccess.getGame(gameID);
            if (Objects.equals(gameData.whiteUsername(), username)) {
                connectionManager.removeWhite(gameID, session);
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                connectionManager.removeBlack(gameID, session);
            } else {
                connectionManager.removeObserver(gameID, session);
            }
        } else {
            throw new ResponseException(ResponseException.Code.authError);
        }
    }

    public GameConnections getConnects(int gameID) {
        return connectionManager.gameConnections(gameID);
    }
}
