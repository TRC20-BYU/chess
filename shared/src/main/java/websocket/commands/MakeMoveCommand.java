package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    ChessMove chessMove;

    public MakeMoveCommand(CommandType command, String auth, int gameID, ChessMove move) {
        super(command, auth, gameID);
        this.chessMove = move;
    }

    public ChessMove getChessMove() {
        return chessMove;
    }
}
