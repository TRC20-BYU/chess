package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece =  board.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        return piece.pieceMoves(board,startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null){
            throw new InvalidMoveException("Can't move nothing");
        }
        if(piece.getTeamColor() != turn){
            throw new InvalidMoveException("Not your turn");
        }
        if(piece.pieceMoves(board,move.getStartPosition()).contains(move)) {
            if (move.getPromotionPiece() != null) {
                board.movePiece(move.getStartPosition(), move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.movePiece(move.getStartPosition(), move.getEndPosition(), piece);
            }
            if (turn == TeamColor.WHITE) {
                turn = TeamColor.BLACK;
            } else {
                turn = TeamColor.WHITE;
            }
        }
        else{
            throw new InvalidMoveException("Invalid move");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition hotSpot;
        hotSpot = kingPos(teamColor);
        return safeSpot(teamColor,hotSpot);
    }

    private ChessPosition kingPos(TeamColor teamColor){
        for(int x = 1; x < 9; x++){
            for(int y = 1; y <9; y++){
                ChessPosition pos = new ChessPosition(x,y);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null){
                    if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                        if (piece.getTeamColor() == teamColor) {
                            return pos;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean safeSpot(TeamColor teamColor, ChessPosition hotSpot)
    {
        Collection<ChessMove> moves = new HashSet<>();
        for(int x = 1; x < 9; x++){
            for(int y = 1; y <9; y++){
                ChessPosition pos = new ChessPosition(x,y);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null){
                    if (piece.getTeamColor() != teamColor) {
                        moves.addAll(piece.pieceMoves(board,pos));
                    }
                }
            }
        }
        Collection<ChessPosition> spots = extractFirst(moves);
        return spots.contains(hotSpot);
    }

    private Collection<ChessPosition> extractFirst(Collection<ChessMove> poses){
        Collection<ChessPosition> spots = new HashSet<>();
        for(ChessMove pos : poses){
            spots.add(pos.getEndPosition());
        }
        return spots;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn=" + turn +
                ", board=" + board +
                '}';
    }
}
