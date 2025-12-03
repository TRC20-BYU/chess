package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    ChessMove move;

    public MakeMoveCommand(CommandType command, String auth, int gameID, ChessMove move) {
        super(command, auth, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
