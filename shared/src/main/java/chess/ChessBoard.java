package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;
    private Collection<ChessPiece> enPassantables = new HashSet<>();

    public ChessBoard() {
         board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow()-1][position.getColumn()-1] = piece;
        if(piece != null) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING || piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                piece.special = true;
            }
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.getRow()-1][position.getColumn()-1];
    }

    public void movePiece(ChessPosition start, ChessPosition end, ChessPiece piece)
    {
        for(ChessPiece pawn : enPassantables){
            if(pawn.getTeamColor() == piece.getTeamColor()){
                pawn.special = false;
                enPassantables.remove(pawn);
            }
        }
        addPiece(end,piece);
        addPiece(start,null);
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && abs(start.getColumn()-end.getColumn()) > 0) {
            addPiece(new ChessPosition(start.getRow(), end.getColumn()),null);
        }
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && abs(start.getRow()-end.getRow()) > 1) {
            enPassantables.add(piece);
        }
        System.out.print(this);
        System.out.print("\n");
    }

    boolean checkEnPassantable(ChessPiece piece){
        return enPassantables.contains(piece);
    }


    public ChessPiece[][] getBoard(){
        ChessPiece[][] boardCopy = new ChessPiece[8][8];
        for(int x =0; x< board.length; x++){
            boardCopy[x] = board[x].clone();
        }
        return boardCopy;
    }

    public void setBoard(ChessPiece[][] board) {
        ChessPiece[][] boardCopy = new ChessPiece[8][8];
        for(int x =0; x< board.length; x++){
            boardCopy[x] = board[x].clone();
        }
        this.board = boardCopy;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        for (int x = 0; x < 8; x++) {
            board[1][x] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][x] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        getPiece(new ChessPosition(1,1)).special = true;
        getPiece(new ChessPosition(8,1)).special = true;
        getPiece(new ChessPosition(1,8)).special = true;
        getPiece(new ChessPosition(8,8)).special = true;
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        getPiece(new ChessPosition(1,5)).special = true;
        getPiece(new ChessPosition(8,5)).special = true;

    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String boardState = "";
        for(int x = board.length - 1; x >= 0; x--) {
            for(int y = 0; y < board[x].length; y++){
                ChessPiece piece = board[x][y];
               boardState +=  String.format("%s", piece == null ? "." : piece.toString() );
            }
            boardState += "\n";
        }
        return boardState;
    }




}
