import datamodel.AuthData;
import datamodel.UserData;
import serverfacade.ServerError;
import serverfacade.ServerFacade;
import ui.EscapeSequences;
import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Welcome ♕ 240 Chess Client ♕ " + EscapeSequences.SET_TEXT_COLOR_GREEN + "- Type Help for menu" + EscapeSequences.RESET_TEXT_COLOR);
        boolean loggedIn = false;
        String user = "";
        String authToken = "";
        String commands = "help login register create list join observe logout quit";
        ServerFacade serverFacade = new ServerFacade("8080");
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
            try {
                if (!loggedIn) {
                    if (Objects.equals(params[0], "help")) {
                        if (params.length != 1) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            preloginUI.help();
                        }
                    }
                    if (Objects.equals(params[0], "login")) {
                        if (params.length != 3) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
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
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            UserData userData = new UserData(params[1], params[2], params[3]);
                            AuthData authData = preloginUI.register(userData);
                            authToken = authData.authToken();
                            user = authData.username();
                            loggedIn = true;

                        }
                    }
                } else {
                    if (Objects.equals(params[0], "help")) {
                        if (params.length != 1) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            postloginUI.help();
                        }
                    }
                    if (Objects.equals(params[0], "logout")) {
                        if (params.length != 1) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            postloginUI.logout(authToken);
                            loggedIn = false;
                        }
                    }
                    if (Objects.equals(params[0], "create")) {
                        if (params.length != 2) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            postloginUI.createGame(authToken, params[1]);
                        }
                    }
                    if (Objects.equals(params[0], "list")) {
                        if (params.length != 1) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            postloginUI.listGame(authToken);
                        }
                    }
                    if (Objects.equals(params[0], "join")) {
                        if (params.length != 3) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            if (Objects.equals(params[2], "WHITE") | Objects.equals(params[2], "BLACK")) {
                                postloginUI.joinGame(authToken, params[1], params[2]);
                            } else {
                                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "The colors are WHITE and BLACK" + EscapeSequences.RESET_TEXT_COLOR);
                            }
                        }
                    }
                    if (Objects.equals(params[0], "observe")) {
                        if (params.length != 2) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            postloginUI.observerGame();
                        }
                    }
                }
            } catch (ServerError serverError) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + serverError.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            }
            if (Objects.equals(params[0], "quit")) {
                if (params.length != 1) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                } else {
                    break;
                }
            }
            if (!commands.contains(params[0])) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: invalid command - help for valid commands" + EscapeSequences.RESET_TEXT_COLOR);
            }

        }
        System.out.println("Good bye");
    }

}