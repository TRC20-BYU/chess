import chess.*;
import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome ♕ 240 Chess Client ♕");
        boolean loggedIn = false;
        PreloginUI preloginUI = new PreloginUI();
        PostloginUI postloginUI = new PostloginUI();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (!loggedIn) {
                if (Objects.equals(line, "help")) {
                    preloginUI.help();
                }
                if (Objects.equals(line, "login")) {
                    preloginUI.login();
                    loggedIn = true;
                }
            } else {
                if (Objects.equals(line, "help")) {
                    postloginUI.help();
                }
                
            }
            if (Objects.equals(line, "quit")) {
                break;
            }

        }
        System.out.println("Good bye");
    }

}