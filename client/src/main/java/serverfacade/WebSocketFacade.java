package serverfacade;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
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
