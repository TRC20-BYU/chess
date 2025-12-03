package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    ChessMove move;

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    String start;
    String end;

    public MakeMoveCommand(CommandType command, String auth, int gameID, ChessMove move, String start, String end) {
        super(command, auth, gameID);
        this.move = move;
        this.start = start;
        this.end = end;
    }

    public ChessMove getMove() {
        return move;
    }
}
