package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
            int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};
            moves = directional(directions,board,myPosition, true);
            //moves = bishopMoves(board,myPosition);
        }
        if (type == PieceType.ROOK)
        {
            int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
            moves = directional(directions,board,myPosition,true);
            //moves = rookMoves(board,myPosition);
        }
        if(type == PieceType.QUEEN) {
            int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
            moves = directional(directions,board,myPosition,true);
        }
        if(type == PieceType.KING){
            int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
            moves = directional(directions,board,myPosition,false);
        }
        if(type == PieceType.KNIGHT){
            int[][] directions = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
            moves = directional(directions,board,myPosition,false);
        }
        return moves;
    }


    Collection<ChessMove> directional(int[][] directions, ChessBoard board, ChessPosition myPosition, boolean follow)
    {
        Collection<ChessMove> moves = new HashSet<>();
        for(int[] dir : directions)
        {
            if(myPosition.getRow() + dir[0] < 9 && myPosition.getColumn() + dir[1] < 9 && myPosition.getRow() + dir[0] >= 1 && myPosition.getColumn() + dir[1] >= 1)
            {
                ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir[0], myPosition.getColumn() + dir[1]);
                if (board.getPiece(newPos) == null)
                {
                    if(follow) {
                        moves.addAll(followPath(board, myPosition, newPos, dir[0], dir[1]));
                    }
                    else{
                        ChessMove cm = new ChessMove(myPosition, newPos, null);
                        moves.add(cm);
                    }
                } else if (board.getPiece(newPos).pieceColor != pieceColor) {
                    ChessMove newCM = new ChessMove(myPosition, newPos, null);
                    moves.add(newCM);
                }

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
            } else if (board.getPiece(newPos).pieceColor != pieceColor) {
                ChessMove newCM = new ChessMove(myPosition, newPos, null);
                moves.add(newCM);
            }
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }
}
