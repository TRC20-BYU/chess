package serverfacade.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.PostloginUI;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    PostloginUI postloginUI;

    public void makeMove(String authToken, int gameID, ChessMove chessMove) {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, chessMove);
            var serializer = new Gson();
            String commandSerialized = serializer.toJson(command);
            session.getBasicRemote().sendText(commandSerialized);
        } catch (IOException e) {

        }
    }

    public void connect(String port, String authToken, int gameID, PostloginUI postloginUI) {
        this.postloginUI = postloginUI;
        URI uri;
        try {
            uri = new URI("ws://localhost:" + port + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            var serializer = new Gson();
            String commandSerialized = serializer.toJson(userGameCommand);
            session.getBasicRemote().sendText(commandSerialized);
        } catch (URISyntaxException | IOException | DeploymentException e) {

        }
    }

    public void leave(String authToken, int gameID) {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            var serializer = new Gson();
            String commandSerialized = serializer.toJson(userGameCommand);
            session.getBasicRemote().sendText(commandSerialized);
        } catch (IOException e) {

        }
    }


    public void ping(String port) throws URISyntaxException, DeploymentException, IOException {
//        URI uri = new URI("ws://localhost:" + port + "/ws");
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        Session session = container.connectToServer(this, uri);
//        session.addMessageHandler(new MessageHandler.Whole<String>() {
//            public void onMessage(String message) {
//                System.out.println(message);
//            }
//        });
//
//        session.getBasicRemote().sendText("testing");
    }

    void onMessage(String message) {
        var serializer = new Gson();
        ServerMessage serverMessage = serializer.fromJson(message, ServerMessage.class);

        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage game = serializer.fromJson(message, LoadGameMessage.class);
            ChessGame chessGame = game.getGame();
            postloginUI.drawBoard(chessGame);
        }
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage errorMessage = serializer.fromJson(message, ErrorMessage.class);
        }
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notificationMessage = serializer.fromJson(message, NotificationMessage.class);
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
