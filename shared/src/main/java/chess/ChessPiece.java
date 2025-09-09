package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    final private ChessPiece.PieceType type;
    final private ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

//    public void SetPieceType()
//    {
//
//    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        if (type == PieceType.BISHOP)
        {
            moves = bishopMoves(board,myPosition);
        }
        return moves;
    }

    Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> moves = new HashSet<>();
        if(myPosition.getRow() + 1 < 9 && myPosition.getColumn() + 1 < 9) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            if (board.getPiece(newPos) == null) {
                //System.out.println(myPosition +" "+ newPos + " 1");
                moves.addAll(followPath(board, myPosition, newPos, 1, 1));
            }
        }
        if(myPosition.getRow() + 1 < 9 && myPosition.getColumn() - 1 >= 1) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            if (board.getPiece(newPos) == null) {
                //System.out.println(myPosition +" "+ newPos + " 2");
                moves.addAll(followPath(board, myPosition, newPos, 1, -1));
            }
        }
        if(myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 1 < 9) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            if (board.getPiece(newPos) == null) {
                //System.out.println(myPosition +" "+ newPos + " 3");
                moves.addAll(followPath(board, myPosition, newPos, -1, 1));
            }
        }
        if(myPosition.getRow() - 1 >= 1 && myPosition.getColumn() - 1 >= 1) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            if (board.getPiece(newPos) == null) {
                //System.out.println(myPosition +" "+ newPos + " 4");
                moves.addAll(followPath(board, myPosition, newPos, -1, -1));
            }
        }
        return moves;
    }

    Collection<ChessMove> followPath(ChessBoard board, ChessPosition myPosition, ChessPosition pos, int Xpos, int Ypos)
    {
        Collection<ChessMove> moves = new HashSet<>();
        ChessMove cm = new ChessMove(myPosition, pos, null);
        moves.add(cm);
        if(pos.getRow() + Xpos < 9 && pos.getColumn() + Ypos < 9 && pos.getRow() + Xpos >= 1 && pos.getColumn() + Ypos >= 1) {
            ChessPosition newPos = new ChessPosition(pos.getRow() + Xpos, pos.getColumn() + Ypos);
            if (board.getPiece(newPos) == null) {
                moves.addAll(followPath(board, myPosition, newPos, Xpos, Ypos));
            }
        }
        return moves;
    }
}
