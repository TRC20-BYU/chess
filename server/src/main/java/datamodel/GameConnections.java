package datamodel;

import org.eclipse.jetty.websocket.api.Session;

import java.util.List;

public class GameConnections {
    Session whitePlayer;
    Session blackPlayer;
    List<Session> observers;

    public void addWhitePlayer(Session session) {
        whitePlayer = session;
    }

    public void addBlackPlayer(Session session) {
        blackPlayer = session;
    }

    public void addObserver(Session session) {
        observers.add(session);
    }

    public void removeWhitePlayer() {
        whitePlayer = null;
    }

    public void removeBlackPlayer() {
        blackPlayer = null;
    }

    public void removeObserver(Session session) {
        observers.remove(session);
    }

    public Session getWhitePlayer() {
        return whitePlayer;
    }

    public Session getBlackPlayer() {
        return blackPlayer;
    }

    public List<Session> getObservers() {
        return observers;
    }


}
