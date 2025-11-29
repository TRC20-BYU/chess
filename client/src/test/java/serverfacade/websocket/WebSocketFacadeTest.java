package serverfacade.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.JoinData;
import datamodel.UserData;
import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;

class WebSocketFacadeTest {
    private static Server server;
    private static WebSocketFacade webSocketFacade;
    static String portLabel;

    @BeforeAll
    public static void init() {
        server = new Server();
        webSocketFacade = new WebSocketFacade();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        portLabel = Integer.toString(port);
    }


    @Test
    void ping() {
        try {
            webSocketFacade.ping(portLabel);
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void makeMove() {
        try {
            String authToken = createGame();
            ChessMove chessMove = new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null);
            webSocketFacade.makeMove(portLabel, authToken, 1, chessMove);
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    String createGame() {
        UserData userData = new UserData("joe", "joe", "joe");
        ServerFacade serverFacade = new ServerFacade(portLabel);
        serverFacade.delete("db", null, null);
        serverFacade.post("user", userData, null);
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        GameData gameData = new GameData(0, null, null, "something", new ChessGame());
        serverFacade.post("game", gameData, authData.authToken());
        JoinData joinData = new JoinData("WHITE", 1);
        serverFacade.put("game", joinData, authData.authToken());
        return authData.authToken();
    }
}
