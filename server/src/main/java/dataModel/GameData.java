package dataModel;

import org.jetbrains.annotations.NotNull;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
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
