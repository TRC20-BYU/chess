package websocket;

public class WebSocketCommands {

    public WebSocketCommands(CommandType command, String auth, int gameID) {
        this.commandType = command;
        this.authToken = auth;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType commandType;
    public String authToken;
    public Integer gameID;

}
