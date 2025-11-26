package serverfacade;

import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

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
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}