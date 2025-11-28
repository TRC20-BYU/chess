package websocket;

import chess.ChessMove;

public class MakeMoveCommand extends WebSocketCommands {
    ChessMove chessMove;

    public MakeMoveCommand(CommandType command, String auth, int gameID, ChessMove move) {
        super(command, auth, gameID);
        this.chessMove = move;
    }
}
