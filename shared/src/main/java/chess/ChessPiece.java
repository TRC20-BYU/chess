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
    boolean hasMoved = true;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        if (type == PieceType.BISHOP) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            moves = directional(directions, board, myPosition, true);
            //moves = bishopMoves(board,myPosition);
        }
        if (type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            moves = directional(directions, board, myPosition, true);
            //moves = rookMoves(board,myPosition);
        }
        if (type == PieceType.QUEEN) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            moves = directional(directions, board, myPosition, true);
        }
        if (type == PieceType.KING) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            moves = directional(directions, board, myPosition, false);
            if (!hasMoved) {
                moves.addAll(checkCastling(board, myPosition));
            }
        }
        if (type == PieceType.KNIGHT) {
            int[][] directions = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {1, -2}, {2, -1}, {-1, -2}, {-2, -1}};
            moves = directional(directions, board, myPosition, false);
        }
        if (type == PieceType.PAWN) {
            moves = pawnMoves(board, myPosition);
        }
        return moves;
    }


    private Collection<ChessMove> directional(int[][] directions, ChessBoard board, ChessPosition myPosition, boolean follow) {
        Collection<ChessMove> moves = new HashSet<>();
        for (int[] dir : directions) {
            if (myPosition.getRow() + dir[0] < 9 && myPosition.getColumn() + dir[1] < 9 && myPosition.getRow() + dir[0] >= 1 && myPosition.getColumn() + dir[1] >= 1) {
                ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir[0], myPosition.getColumn() + dir[1]);
                if (board.getPiece(newPos) == null) {
                    if (follow) {
                        moves.addAll(followPath(board, myPosition, newPos, dir[0], dir[1]));
                    } else {
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


    private Collection<ChessMove> followPath(ChessBoard board, ChessPosition myPosition, ChessPosition pos, int xpos, int Ypos) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessMove cm = new ChessMove(myPosition, pos, null);
        moves.add(cm);
        if (pos.getRow() + xpos < 9 && pos.getColumn() + Ypos < 9 && pos.getRow() + xpos >= 1 && pos.getColumn() + Ypos >= 1) {
            ChessPosition newPos = new ChessPosition(pos.getRow() + xpos, pos.getColumn() + Ypos);
            if (board.getPiece(newPos) == null) {
                moves.addAll(followPath(board, myPosition, newPos, xpos, Ypos));
            } else if (board.getPiece(newPos).pieceColor != pieceColor) {
                ChessMove newCM = new ChessMove(myPosition, newPos, null);
                moves.add(newCM);
            }
        }
        return moves;
    }

    private Collection<ChessMove> checkCastling(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        if (board.getPiece(new ChessPosition(myPosition.getRow(), 8)) != null) {
            if (!board.getPiece(new ChessPosition(myPosition.getRow(), 8)).hasMoved) {
                boolean clear = true;
                for (int x = myPosition.getColumn() + 1; x < 8; x++) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow(), x)) != null) {
                        clear = false;
                    }
                }
                if (clear) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), 7), null));
                }
            }
        }

        if (board.getPiece(new ChessPosition(myPosition.getRow(), 1)) != null) {
            if (board.getPiece(new ChessPosition(myPosition.getRow(), 1)).hasMoved) {
                ChessPiece piece = board.getPiece(new ChessPosition(myPosition.getRow(), 1));
                System.out.print(piece);
                System.out.print("\n");
            }
            if (!board.getPiece(new ChessPosition(myPosition.getRow(), 1)).hasMoved) {
                boolean clear = true;
                for (int x = myPosition.getColumn() - 1; x > 1; x--) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow(), x)) != null) {
                        clear = false;
                    }
                }
                if (clear) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), 3), null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        int dir = 1;
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            dir = -1;
        }
        Collection<ChessMove> moves = new HashSet<>();
        if ((myPosition.getRow() + dir) < 9 && (myPosition.getRow() + dir) > 0) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn());
            if (board.getPiece(newPos) == null) {
                if (newPos.getRow() == 8) {
                    moves.addAll(pawnPromotions(myPosition, newPos));
                } else if (newPos.getRow() == 1) {
                    moves.addAll(pawnPromotions(myPosition, newPos));
                } else {
                    ChessMove cm = new ChessMove(myPosition, newPos, null);
                    moves.add(cm);
                }
                if (myPosition.getRow() == 2 || myPosition.getRow() == 7) {
                    if ((myPosition.getRow() + (dir + dir)) < 9 && (myPosition.getRow() + (dir + dir)) > 0) {
                        newPos = new ChessPosition(myPosition.getRow() + (dir * 2), myPosition.getColumn());
                        if (board.getPiece(newPos) == null) {
                            ChessMove cm = new ChessMove(myPosition, newPos, null);
                            moves.add(cm);
                        }
                    }
                }
            }
            if (myPosition.getColumn() + 1 < 9) {
                newPos = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() + 1);
                checkPawnAttack(board, myPosition, moves, newPos);
            }
            if (myPosition.getColumn() - 1 > 0) {
                newPos = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() - 1);
                checkPawnAttack(board, myPosition, moves, newPos);
            }
        }
        return moves;
    }

    private void checkPawnAttack(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, ChessPosition newPos) {
        if (board.getPiece(newPos) != null) {
            if (board.getPiece(newPos).pieceColor != pieceColor) {
                if (newPos.getRow() == 8) {
                    moves.addAll(pawnPromotions(myPosition, newPos));
                } else if (newPos.getRow() == 1) {
                    moves.addAll(pawnPromotions(myPosition, newPos));
                } else {
                    ChessMove cm = new ChessMove(myPosition, newPos, null);
                    moves.add(cm);
                }
            }
        }
        ChessPiece right = null;
        if (myPosition.getColumn() + 1 < 9) {
            right = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1));
        }
        ChessPiece left = null;
        if (myPosition.getColumn() - 1 > 0) {
            left = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1));
        }
        if (right != null || left != null) {
            int dir = 1;
            if (getTeamColor() == ChessGame.TeamColor.BLACK) {
                dir = -1;
            }
            if (right != null) {
                if (board.checkEnPassantable(board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)))) {
                    ChessMove cm = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() + 1), null);
                    moves.add(cm);
                }
            }

            if (left != null) {
                if (board.checkEnPassantable(board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)))) {
                    ChessMove cm = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() - 1), null);
                    moves.add(cm);
                }
            }
        }
    }


    private Collection<ChessMove> pawnPromotions(ChessPosition myPosition, ChessPosition newPos) {
        Collection<ChessMove> moves = new HashSet<>();
        for (PieceType piece : PieceType.values()) {
            if (piece != PieceType.PAWN) {
                if (piece != PieceType.KING) {
                    ChessMove cm = new ChessMove(myPosition, newPos, piece);
                    moves.add(cm);
                }
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

    @Override
    public String toString() {
        String s = String.format("%s", type.name().toCharArray()[0]);
        if (type == PieceType.KNIGHT) {
            s = "N";
        }
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            s = s.toLowerCase();
        }

        return s;
    }
}
