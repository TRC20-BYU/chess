package websocket;

import datamodel.GameConnections;
import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    ConcurrentHashMap<Integer, GameConnections> gameConnectins = new ConcurrentHashMap<>();

    public void addObserver(int gameID, Session session) {
        if (gameConnectins.containsKey(gameID)) {
            gameConnectins.get(gameID).addObserver(session);
        } else {
            gameConnectins.put(gameID, new GameConnections());
            gameConnectins.get(gameID).addObserver(session);
        }
    }

    public void addWhite(int gameID, Session session) {
        if (gameConnectins.containsKey(gameID)) {
            gameConnectins.get(gameID).addWhitePlayer(session);
        } else {
            gameConnectins.put(gameID, new GameConnections());
            gameConnectins.get(gameID).addWhitePlayer(session);
        }
    }

    public void addBlack(int gameID, Session session) {
        if (gameConnectins.containsKey(gameID)) {
            gameConnectins.get(gameID).addBlackPlayer(session);
        } else {
            gameConnectins.put(gameID, new GameConnections());
            gameConnectins.get(gameID).addBlackPlayer(session);
        }
    }

    public void removeObserver(int gameID, Session session) {
        if (gameConnectins.containsKey(gameID)) {
            gameConnectins.get(gameID).removeObserver(session);
        } else {
            gameConnectins.put(gameID, new GameConnections());
        }
    }

    public void removeWhite(int gameID, Session session) {
        if (gameConnectins.containsKey(gameID)) {
            gameConnectins.get(gameID).removeWhitePlayer();
        } else {
            gameConnectins.put(gameID, new GameConnections());
        }
    }

    public void removeBlack(int gameID, Session session) {
        if (gameConnectins.containsKey(gameID)) {
            gameConnectins.get(gameID).removeBlackPlayer();
        } else {
            gameConnectins.put(gameID, new GameConnections());
        }
    }
}
