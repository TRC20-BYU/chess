package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ResponseException;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
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
        System.out.println("Websocket message received");
        try {
            var serializer = new Gson();
            String reqJson = ctx.message();
            var req = serializer.fromJson(reqJson, UserGameCommand.class);
            if (req.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                ChessGame chessGame = moveHandler(reqJson);
                loadGame(ctx, chessGame);
            }
            if (req.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                Session session = ctx.session;
                ChessGame chessGame = gameService.connectService(req.getAuthToken(), req.getGameID(), session);
                loadGame(ctx, chessGame);
            }
            if (req.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                Session session = ctx.session;
                gameService.disconnectService(req.getAuthToken(), req.getGameID(), session);
            }
            if (req.getCommandType() == UserGameCommand.CommandType.RESIGN) {
//            Session session = ctx.session;
//            gameService.resignService(req.getAuthToken(), req.getGameID(),session);
            }
        } catch (Exception e) {
            System.out.println("Websocket error");
        }
    }

    private ChessGame moveHandler(String reqJson) throws SocketException {
        var serializer = new Gson();
        var moveReq = serializer.fromJson(reqJson, MakeMoveCommand.class);
        String authToken = moveReq.getAuthToken();
        int gameId = moveReq.getGameID();
        ChessMove chessMove = moveReq.getChessMove();
        try {
            return gameService.makeMove(authToken, gameId, chessMove);

        } catch (ResponseException e) {
            throw new SocketException("Move failed");
        }
    }

    public void loadGame(@NotNull WsMessageContext ctx, ChessGame chessGame) {
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

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket disconnected");
    }
}
