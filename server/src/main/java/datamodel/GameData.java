package datamodel;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame) {

    @Override
    public String toString() {
        return "\"gameID\": " + gameID +
                ", \"whiteUsername\": " + whiteUsername +
                ", \"blackUsername\": " + blackUsername +
                ", \"gameName\": " + gameName +
                '}';
    }
}
