import chess.*;
import ui.PreloginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        PreloginUI preloginUI = new PreloginUI();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (Objects.equals(line, "help")) {
                preloginUI.help();
            }
        }
    }
}