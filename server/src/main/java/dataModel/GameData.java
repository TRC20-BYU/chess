package dataModel;

import org.jetbrains.annotations.NotNull;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
    @NotNull
    @Override
    public String toString() {
        return "{\"gameID\": " + gameID + ", \"whiteUsername\":" + whiteUsername + ", \"blackUsername\":" + blackUsername + ", \"gameName:\"" + gameName + "}";
    }
}
