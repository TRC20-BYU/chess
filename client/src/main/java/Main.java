import chess.*;
import ui.PreloginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome ♕ 240 Chess Client ♕");
        boolean loggedIn = false;
        PreloginUI preloginUI = new PreloginUI();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (!loggedIn) {
                if (Objects.equals(line, "help")) {
                    preloginUI.help();
                }
                if (Objects.equals(line, "help")) {
                    preloginUI.log
                }
            }
            if (Objects.equals(line, "quit")) {
                break;
            }

        }
        System.out.println("Good bye");
    }

}