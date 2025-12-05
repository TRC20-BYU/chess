package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import serverfacade.InvalidError;
import serverfacade.ServerError;
import serverfacade.websocket.WebSocketFacade;

import java.util.Objects;
import java.util.Scanner;

public class WebSocketUI {

    WebSocketFacade webSocketFacade;
    String port;
    PostloginUI postloginUI;


    public WebSocketUI(WebSocketFacade webSocketFacade, String port) {
        this.webSocketFacade = webSocketFacade;
        this.port = port;
    }

    public void setPostLoginUI(PostloginUI postloginUI) {
        this.postloginUI = postloginUI;
    }

    public void help() {
        //Displays text informing the user what actions they can take.
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "move "
                + EscapeSequences.SET_TEXT_COLOR_BLUE + "- moves a piece");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "highlight "
                + EscapeSequences.SET_TEXT_COLOR_BLUE + "- highlights possible moves for a given piece");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "redraw "
                + EscapeSequences.SET_TEXT_COLOR_BLUE + "- redraws the board");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "leave "
                + EscapeSequences.SET_TEXT_COLOR_BLUE + "- leaves the game");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "resign "
                + EscapeSequences.SET_TEXT_COLOR_BLUE + "- resigns the game");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "help "
                + EscapeSequences.SET_TEXT_COLOR_BLUE
                + "- displays possible commands" + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void connect(int gameID, String authToken) {
        webSocketFacade.connect(port, authToken, gameID, postloginUI);
    }


    public boolean checkForPromotion(ChessMove chessMove) {
        ChessGame chessGame = postloginUI.chess;
        if (chessGame.getBoard().getPiece(chessMove.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN) {
            return chessMove.getEndPosition().getRow() == 8 || chessMove.getEndPosition().getRow() == 1;
        }
        return false;
    }


    public void getChessMove(WebSocketUI webSocketUI, String authToken, int gameID) throws InvalidError {
        System.out.print("Piece to move: ");
        Scanner scanner = new Scanner(System.in);
        String piece = scanner.nextLine();
        ChessPosition pieceLocal = validatePos(piece);
        System.out.print("New space: ");
        scanner = new Scanner(System.in);
        String space = scanner.nextLine();
        ChessPosition newPos = validatePos(space);
        ChessMove chessMove = new ChessMove(pieceLocal, newPos, null);
        if (validateMove(chessMove)) {
            if (webSocketUI.checkForPromotion(chessMove)) {
                ChessPiece.PieceType promotion = getPromotion();
                chessMove = new ChessMove(pieceLocal, newPos, promotion);
            }
            webSocketFacade.makeMove(authToken, gameID, chessMove, piece, space);
        } else {
            throw new ServerError(EscapeSequences.SET_TEXT_COLOR_RED + "Error: invalid move"
                    + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private boolean validateMove(ChessMove chessMove) {
        if (postloginUI.chess.validMoves(chessMove.getStartPosition()) == null) {
            return false;
        }
        return postloginUI.chess.validMoves(chessMove.getStartPosition()).contains(chessMove);
    }

    private static ChessPiece.PieceType getPromotion() {
        System.out.print("Promotion piece: ");
        Scanner scanner = new Scanner(System.in);
        String piece = scanner.nextLine();
        return switch (piece) {
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> throw new InvalidError(EscapeSequences.SET_TEXT_COLOR_RED + "Not valid promotion"
                    + EscapeSequences.RESET_TEXT_COLOR);
        };
    }


    private static ChessPosition validatePos(String coord) throws InvalidError {
        if (coord.length() != 2) {
            throw new InvalidError(EscapeSequences.SET_TEXT_COLOR_RED + "Not valid chess coordinates"
                    + EscapeSequences.RESET_TEXT_COLOR);
        } else {
            int col = switch (coord.charAt(0)) {
                case 'A', 'a' -> 1;
                case 'B', 'b' -> 2;
                case 'C', 'c' -> 3;
                case 'D', 'd' -> 4;
                case 'E', 'e' -> 5;
                case 'F', 'f' -> 6;
                case 'G', 'g' -> 7;
                case 'H', 'h' -> 8;
                default -> throw new InvalidError(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Not valid chess coordinates" + EscapeSequences.RESET_TEXT_COLOR);
            };
            int row;
            try {
                row = Integer.parseInt(String.valueOf(coord.charAt(1)));
                if (row < 1 || row > 8) {
                    throw new InvalidError(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Not valid chess coordinates" + EscapeSequences.RESET_TEXT_COLOR);
                }
            } catch (NumberFormatException e) {
                throw new InvalidError(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Not valid chess coordinates" + EscapeSequences.RESET_TEXT_COLOR);
            }
            return new ChessPosition(row, col);
        }
    }

    public void leave(String authToken, int gameID) {
        webSocketFacade.leave(authToken, gameID);
    }


    public void redraw() {
        postloginUI.redrawBoard();
    }

    public boolean resign(String authToken, int gameID) {
        String answer = "";
        boolean correctAnswer = false;
        while (!correctAnswer) {
            System.out.print("Are you sure (y/n): ");
            Scanner scanner = new Scanner(System.in);
            answer = scanner.nextLine();
            correctAnswer = (Objects.equals(answer, "y") || Objects.equals(answer, "n"));
            if (Objects.equals(answer, "y")) {
                webSocketFacade.resign(authToken, gameID);
                return true;
            }
        }
        return false;
    }

    public void highlight() {
        System.out.print("Piece to highlight: ");
        Scanner scanner = new Scanner(System.in);
        String piece = scanner.nextLine();
        ChessPosition pieceLocal = validatePos(piece);
        postloginUI.highlight(pieceLocal);
    }
}
