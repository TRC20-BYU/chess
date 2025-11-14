import datamodel.AuthData;
import datamodel.UserData;
import serverfacade.ServerFacade;
import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome ♕ 240 Chess Client ♕");
        boolean loggedIn = false;
        String user = "";
        String authToken = "";
        ServerFacade serverFacade = new ServerFacade();
        PreloginUI preloginUI = new PreloginUI(serverFacade);
        PostloginUI postloginUI = new PostloginUI(serverFacade);


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
                    if (params.length != 1) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        preloginUI.help();
                    }
                }
                if (Objects.equals(params[0], "login")) {
                    if (params.length != 3) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        UserData userData = new UserData(params[1], params[2], null);
                        AuthData authData = preloginUI.login(userData);
                        authToken = authData.authToken();
                        user = authData.username();
                        loggedIn = true;
                    }
                }
                if (Objects.equals(params[0], "register")) {
                    if (params.length != 4) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        UserData userData = new UserData(params[1], params[2], params[3]);
                        if (preloginUI.register(userData)) {
                            user = params[1];
                            loggedIn = true;
                        }
                    }
                }
            } else {
                if (Objects.equals(params[0], "help")) {
                    if (params.length != 1) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        postloginUI.help();
                    }
                }
                if (Objects.equals(params[0], "logout")) {
                    if (params.length != 1) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        postloginUI.logout(authToken);
                        loggedIn = false;
                    }
                }
                if (Objects.equals(params[0], "create")) {
                    if (params.length != 2) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        postloginUI.createGame(authToken, params[1]);
                    }
                }
                if (Objects.equals(params[0], "list")) {
                    if (params.length != 2) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        postloginUI.listGame(authToken);
                    }
                }
                if (Objects.equals(params[0], "join")) {
                    if (params.length != 3) {
                        System.out.println("Error: incorrect number of arguments");
                    } else {
                        if (Objects.equals(params[2], "WHITE") | Objects.equals(params[2], "BLACK")) {
                            postloginUI.joinGame(authToken, params[1], params[2]);
                        } else {
                            System.out.println("The colors are WHITE and BLACK");
                        }
                    }
                }
            }
            if (Objects.equals(params[0], "quit")) {
                if (params.length != 1) {
                    System.out.println("Error: incorrect number of arguments");
                } else {
                    break;
                }
            }

        }
        System.out.println("Good bye");
    }

}