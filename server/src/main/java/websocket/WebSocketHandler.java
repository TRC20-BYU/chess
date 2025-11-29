package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import server.ResponseException;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

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
        var serializer = new Gson();
        String reqJson = ctx.message();
        var req = serializer.fromJson(reqJson, UserGameCommand.class);
        if (req.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            moveHandler(reqJson);
        }
        if (req.getCommandType() == UserGameCommand.CommandType.CONNECT) {

        }
        if (req.getCommandType() == UserGameCommand.CommandType.LEAVE) {

        }
        if (req.getCommandType() == UserGameCommand.CommandType.RESIGN) {

        }
    }

    private void moveHandler(String reqJson) {
        var serializer = new Gson();
        var moveReq = serializer.fromJson(reqJson, MakeMoveCommand.class);
        String authToken = moveReq.getAuthToken();
        int gameId = moveReq.getGameID();
        ChessMove chessMove = moveReq.getChessMove();
        try {
            gameService.makeMove(authToken, gameId, chessMove);
        } catch (ResponseException e) {

        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket disconnected");
    }
}
