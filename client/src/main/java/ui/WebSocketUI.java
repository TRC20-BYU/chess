package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import serverfacade.InvalidError;
import serverfacade.websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Scanner;

public class WebSocketUI {

    WebSocketFacade webSocketFacade;
    ChessGame chessGame;
    String port;


    public WebSocketUI(WebSocketFacade webSocketFacade, String port) {
        this.webSocketFacade = webSocketFacade;
        this.port = port;
    }

    public void connect(int gameID, String authToken) {
        webSocketFacade.connect(port, authToken, gameID);
    }

    public void move(ChessMove chessMove, String authToken, int gameID) {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, chessMove);
    }

    public boolean checkForPromotion(ChessMove chessMove) {
        if (chessGame.getBoard().getPiece(chessMove.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN) {
            return chessMove.getEndPosition().getRow() == 8 || chessMove.getEndPosition().getRow() == 1;
        }
        return false;
    }


    public void getChessMove(WebSocketUI webSocketUI, String authToken, int gameID) {
        System.out.print("Piece to move: ");
        Scanner scanner = new Scanner(System.in);
        String piece = scanner.nextLine();
        ChessPosition pieceLocal = validateMove(piece);
        System.out.print("New space: ");
        scanner = new Scanner(System.in);
        String space = scanner.nextLine();
        ChessPosition newPos = validateMove(space);
        ChessMove chessMove = new ChessMove(pieceLocal, newPos, null);
        if (webSocketUI.checkForPromotion(chessMove)) {
            ChessPiece.PieceType promotion = getPromotion();
            chessMove = new ChessMove(pieceLocal, newPos, promotion);
        }
        webSocketUI.move(chessMove, authToken, gameID);
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
            default -> throw new InvalidError("Not valid promotion");
        };
    }


    private static ChessPosition validateMove(String coord) {
        if (coord.length() != 2) {
            throw new InvalidError("Not valid chess coordinates");
        } else {
            int row = switch (coord.charAt(0)) {
                case 'A' -> 1;
                case 'B' -> 2;
                case 'C' -> 3;
                case 'D' -> 4;
                case 'E' -> 5;
                case 'F' -> 6;
                case 'G' -> 7;
                case 'H' -> 8;
                default -> throw new InvalidError("Not valid chess coordinates");
            };
            int col;
            try {
                col = Integer.parseInt(String.valueOf(coord.charAt(0)));
                if (col < 0 || col > 8) {
                    throw new InvalidError("Not valid chess coordinates");
                }
            } catch (NumberFormatException e) {
                throw new InvalidError("Not valid chess coordinates");
            }
            return new ChessPosition(row, col);
        }
    }
}
