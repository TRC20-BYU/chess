package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import datamodel.GameConnections;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ResponseException;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.SocketException;

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
                ChessGame chessGame = moveHandler(reqJson);
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                loadGame(ctx, chessGame, gameConnections);
                String username = gameService.getGameData(req.getAuthToken());

                notifyAction(gameConnections, username, req.getGameID(), ctx, "has made a move in game");
            }
            if (req.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                System.out.println("Websocket connect received");
                Session session = ctx.session;
                ChessGame chessGame = gameService.connectService(req.getAuthToken(), req.getGameID(), session);
                String username = gameService.getGameData(req.getAuthToken());
                loadGameSingle(ctx, chessGame);
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                notifyAction(gameConnections, username, req.getGameID(), ctx, "has joined game");
            }
            if (req.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                System.out.println("Websocket leave received");
                Session session = ctx.session;
                gameService.disconnectService(req.getAuthToken(), req.getGameID(), session);
                String username = gameService.getGameData(req.getAuthToken());
                GameConnections gameConnections = gameService.getConnects(req.getGameID());
                notifyAction(gameConnections, username, req.getGameID(), ctx, "has left game");
            }
            if (req.getCommandType() == UserGameCommand.CommandType.RESIGN) {
//            Session session = ctx.session;
//            gameService.resignService(req.getAuthToken(), req.getGameID(),session);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ChessGame moveHandler(String reqJson) throws SocketException {
        var serializer = new Gson();
        var moveReq = serializer.fromJson(reqJson, MakeMoveCommand.class);
        String authToken = moveReq.getAuthToken();
        int gameId = moveReq.getGameID();
        ChessMove chessMove = moveReq.getMove();
        try {
            return gameService.makeMove(authToken, gameId, chessMove);

        } catch (ResponseException e) {
            throw new SocketException("Move failed");
        }
    }

    public void loadGame(@NotNull WsMessageContext ctx, ChessGame chessGame, GameConnections gameConnections) throws IOException {
        System.out.println("Web socket load");
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
        var serializer = new Gson();
        String serialized = serializer.toJson(loadGameMessage);
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


    public void notifyAction(GameConnections gameConnections, String username, int gameID, WsMessageContext ctx, String action) throws IOException {
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


    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket disconnected");
    }
}
