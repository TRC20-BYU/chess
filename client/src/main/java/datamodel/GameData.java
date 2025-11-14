package datamodel;

public class GameData {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public int getGameID() {
        return gameID;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setBlackUsername(String username) {
        blackUsername = username;
    }

    public void setWhiteUsername(String username) {
        whiteUsername = username;
    }

    @Override
    public String toString() {
        return "\"gameID\": " + gameID +
                ", \"whiteUsername\": " + whiteUsername +
                ", \"blackUsername\": " + blackUsername +
                ", \"gameName\": " + gameName +
                '}';
    }
}
