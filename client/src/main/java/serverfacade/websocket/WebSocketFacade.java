package serverfacade.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    public void makeMove(String port, String authToken, int gameID, ChessMove chessMove) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(this, uri);
        MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, chessMove);
        var serializer = new Gson();
        String commandSerialized = serializer.toJson(command);
        session.getBasicRemote().sendText(commandSerialized);
    }


    public void ping(String port) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(this, uri);
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });

        session.getBasicRemote().sendText("testing");
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
