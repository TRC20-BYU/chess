package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import datamodel.GameData;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        var serializer = new Gson();
        String reqJson = ctx.message();
        var req = serializer.fromJson(reqJson, WebSocketCommands.class);
        if (req.commandType == WebSocketCommands.CommandType.MAKE_MOVE) {
            moveHandler(reqJson);
        }
        if (req.commandType == WebSocketCommands.CommandType.CONNECT) {

        }
        if (req.commandType == WebSocketCommands.CommandType.LEAVE) {

        }
        if (req.commandType == WebSocketCommands.CommandType.RESIGN) {

        }
    }

    private void moveHandler(String reqJson) {
        var serializer = new Gson();
        var moveReq = serializer.fromJson(reqJson, MakeMoveCommand.class);
        String authToken = moveReq.authToken;
        int GameId = moveReq.gameID;
        ChessMove chessMove = moveReq.chessMove;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket disconnected");
    }
}
