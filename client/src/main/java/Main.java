import chess.*;
import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome ♕ 240 Chess Client ♕");
        boolean loggedIn = false;
        String user = "";
        PreloginUI preloginUI = new PreloginUI();
        PostloginUI postloginUI = new PostloginUI();

        while (true) {
            if (!loggedIn) {
                System.out.print("not logged in: ");
            } else {
                System.out.print("logged in as " + user + ": ");
            }
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] params = line.split(" ");
            if (!loggedIn) {
                if (Objects.equals(params[0], "help")) {
                    preloginUI.help();
                }
                if (Objects.equals(params[0], "login")) {
                    preloginUI.login();
                    user = params[1];
                    loggedIn = true;
                }
            } else {
                if (Objects.equals(params[0], "help")) {
                    postloginUI.help();
                }
                if (Objects.equals(params[0], "logout")) {
                    postloginUI.logout();
                    loggedIn = false;
                }
            }
            if (Objects.equals(params[0], "quit")) {
                break;
            }

        }
        System.out.println("Good bye");
    }

}