package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import datamodel.GameConnections;
import datamodel.GameData;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ResponseException;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    GameService gameService;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {

        try {
            var serializer = new Gson();
            String reqJson = ctx.message();
            var req = serializer.fromJson(reqJson, UserGameCommand.class);
            if (req.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                System.out.println("Websocket move received");
                MakeMoveCommand moveReq = serializer.fromJson(reqJson, MakeMoveCommand.class);
                ChessGame chessGame = moveHandler(moveReq);
                GameData gameData = gameService.getGameData(req.getGameID());
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                loadGame(ctx, chessGame, gameConnections);
                String username = gameService.getUsername(req.getAuthToken());
                notifyAction(gameConnections, username, req.getGameID(), ctx, "has moved " +
                        moveReq.getStart() + " to " + moveReq.getEnd() + " in game");
                checkCheckMate(gameData, req.getGameID(), gameConnections);
            }
            if (req.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                System.out.println("Websocket connect received");
                Session session = ctx.session;
                GameData chessGame = gameService.connectService(req.getAuthToken(), req.getGameID(), session);
                String username = gameService.getUsername(req.getAuthToken());
                loadGameSingle(ctx, chessGame.chessGame());
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                if (Objects.equals(username, chessGame.whiteUsername())) {
                    notifyAction(gameConnections, username, req.getGameID(), ctx, "has joined game as white");
                } else if (Objects.equals(username, chessGame.blackUsername())) {
                    notifyAction(gameConnections, username, req.getGameID(), ctx, "has joined game as black");
                } else {
                    notifyAction(gameConnections, username, req.getGameID(), ctx, "is observing game");
                }
            }
            if (req.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                System.out.println("Websocket leave received");
                Session session = ctx.session;
                gameService.disconnectService(req.getAuthToken(), req.getGameID(), session);
                String username = gameService.getUsername(req.getAuthToken());
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                notifyAction(gameConnections, username, req.getGameID(), ctx, "has left game");
            }
            if (req.getCommandType() == UserGameCommand.CommandType.RESIGN) {
                gameService.resign(req.getAuthToken(), req.getGameID());
                String username = gameService.getUsername(req.getAuthToken());
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                notifyActionAll(gameConnections, username, req.getGameID(), "has resigned game");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            sendError(e.getMessage(), ctx);
        } catch (ResponseException e) {
            if (e.getCode() == ResponseException.Code.authError) {
                e.printStackTrace();
                sendError("you are not authorized to do that", ctx);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendError("something went wrong", ctx);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkCheckMate(GameData chessGame, int gameId, GameConnections gameConnections) throws IOException {
        if (chessGame.chessGame().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            String username = chessGame.whiteUsername();
            notifyActionAll(gameConnections, username, gameId, " is in checkmate as white in game");
        } else if (chessGame.chessGame().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            String username = chessGame.blackUsername();
            notifyActionAll(gameConnections, username, gameId, " is in checkmate as black in game");
        } else if (chessGame.chessGame().isInCheck(ChessGame.TeamColor.WHITE)) {
            String username = chessGame.whiteUsername();
            notifyActionAll(gameConnections, username, gameId, " is in check as white in game");
        } else if (chessGame.chessGame().isInCheck(ChessGame.TeamColor.BLACK)) {
            String username = chessGame.whiteUsername();
            notifyActionAll(gameConnections, username, gameId, " is in check as black in game");
        }
    }

    private ChessGame moveHandler(MakeMoveCommand moveReq) throws SocketException {
        String authToken = moveReq.getAuthToken();
        int gameId = moveReq.getGameID();
        ChessMove chessMove = moveReq.getMove();
        try {
            return gameService.makeMove(authToken, gameId, chessMove);
        } catch (ResponseException e) {
            throw new SocketException("Move failed");
        }
    }

    public void loadGame(@NotNull WsMessageContext ctx, ChessGame chessGame, GameConnections gameConnections)
            throws IOException {
        System.out.println("Web socket load");
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
        var serializer = new Gson();
        String serialized = serializer.toJson(loadGameMessage);
        sendMessageToAll(gameConnections, serialized);
    }

    private void sendMessageToAll(GameConnections gameConnections, String serialized) throws IOException {
        if (gameConnections.getWhitePlayer() != null) {
            gameConnections.getWhitePlayer().getRemote().sendString(serialized);
        }
        if (gameConnections.getBlackPlayer() != null) {
            gameConnections.getBlackPlayer().getRemote().sendString(serialized);
        }

        for (Session obs : gameConnections.getObservers()) {
            if (obs != null) {
                obs.getRemote().sendString(serialized);
            }
        }
    }

    public void loadGameSingle(@NotNull WsMessageContext ctx, ChessGame chessGame) {
        System.out.println("Web socket load");
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
        var serializer = new Gson();
        String serializedMessage = serializer.toJson(loadGameMessage);
        try {
            ctx.session.getRemote().sendString(serializedMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendError(String error, WsMessageContext ctx) {
        Session session = ctx.session;
        var serializer = new Gson();
        String message = "Error:" + error;
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        String serializedMessage = serializer.toJson(errorMessage);
        try {
            session.getRemote().sendString(serializedMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void notifyAction(GameConnections gameConnections, String username,
                             int gameID, WsMessageContext ctx, String action) throws IOException {
        Session session = ctx.session;
        var serializer = new Gson();
        String message = username + " " + action + " " + gameID;
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String serialized = serializer.toJson(notificationMessage);
        if (gameConnections.getWhitePlayer() != session && gameConnections.getWhitePlayer() != null) {
            gameConnections.getWhitePlayer().getRemote().sendString(serialized);
        }
        if (gameConnections.getBlackPlayer() != session && gameConnections.getBlackPlayer() != null) {
            gameConnections.getBlackPlayer().getRemote().sendString(serialized);
        }

        for (Session obs : gameConnections.getObservers()) {
            if (obs != session && obs != null) {
                obs.getRemote().sendString(serialized);
            }
        }
    }

    public void notifyActionAll(GameConnections gameConnections, String username, int gameID, String action) throws IOException {
        var serializer = new Gson();
        String message = username + " " + action + " " + gameID;
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String serialized = serializer.toJson(notificationMessage);
        sendMessageToAll(gameConnections, serialized);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket disconnected");
    }
}
