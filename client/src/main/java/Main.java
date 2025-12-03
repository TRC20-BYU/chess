import datamodel.AuthData;
import datamodel.UserData;
import serverfacade.ServerError;
import serverfacade.ServerFacade;
import serverfacade.websocket.WebSocketFacade;
import ui.EscapeSequences;
import ui.PostloginUI;
import ui.PreloginUI;
import ui.WebSocketUI;

import java.util.Objects;
import java.util.Scanner;


public class Main {

    static int loggedIn = 0;
    static String user = "";
    static String authToken = "";
    static String port = "8080";
    static int gameID = 0;

    public static void main(String[] args) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Welcome ♕ 240 Chess Client ♕ " +
                EscapeSequences.SET_TEXT_COLOR_GREEN + "- Type Help for options" + EscapeSequences.RESET_TEXT_COLOR);
        String commands = "help login register create list join observe logout quit move leave redraw resign highlight";
        ServerFacade serverFacade = new ServerFacade(port);
        PreloginUI preloginUI = new PreloginUI(serverFacade);
        WebSocketFacade webSocketFacade = new WebSocketFacade();
        WebSocketUI webSocketUI = new WebSocketUI(webSocketFacade, port);
        PostloginUI postloginUI = new PostloginUI(serverFacade, webSocketUI);


        while (true) {
            if (loggedIn == 0) {
                System.out.print("not logged in: ");
            } else if (loggedIn == 1) {
                System.out.print("logged in as " + user + ": ");
            }
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] params = line.split(" ");
            try {
                if (loggedIn == 0) {
                    preLoginOptions(params, preloginUI);
                } else if (loggedIn == 1) {
                    postLoginOptions(params, postloginUI, webSocketUI);
                } else {
                    webSocketOptions(params, webSocketUI);
                }
            } catch (ServerError serverError) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + serverError.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            }
            if (Objects.equals(params[0], "quit")) {
                if (params.length != 1) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
                } else {
                    break;
                }
            }
            if (!commands.contains(params[0])) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: invalid command - help for valid commands" + EscapeSequences.RESET_TEXT_COLOR);
            }

        }
        System.out.println("Good bye");
    }

    private static void webSocketOptions(String[] params, WebSocketUI webSocketUI) {
        if (Objects.equals(params[0], "move")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                webSocketUI.getChessMove(webSocketUI, authToken, gameID);
            }
        }
        if (Objects.equals(params[0], "leave")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                webSocketUI.leave(authToken, gameID);
                loggedIn = 1;
            }
        }
        if (Objects.equals(params[0], "resign")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                webSocketUI.resign(authToken, gameID);
            }
        }
        if (Objects.equals(params[0], "redraw")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                webSocketUI.redraw();
            }
        }
        if (Objects.equals(params[0], "help")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                webSocketUI.help();
            }
        }
        if (Objects.equals(params[0], "highlight")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                webSocketUI.highlight();
            }
        }
    }


    private static void postLoginOptions(String[] params, PostloginUI postloginUI, WebSocketUI webSocketUI) {
        if (Objects.equals(params[0], "help")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                postloginUI.help();
            }
        }
        if (Objects.equals(params[0], "logout")) {
            if (params.length != 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                postloginUI.logout(authToken);
                loggedIn = 0;
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
                    gameID = postloginUI.joinGame(authToken, params[1], params[2]);
                    webSocketUI.connect(gameID, authToken);
                    loggedIn = 2;
                } else {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "The colors are WHITE and BLACK" + EscapeSequences.RESET_TEXT_COLOR);
                }
            }
        }
        if (Objects.equals(params[0], "observe")) {
            if (params.length != 2) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: incorrect number of arguments" + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                gameID = postloginUI.observerGame(params[1], authToken);
                loggedIn = 2;
            }
        }
    }

    private static void preLoginOptions(String[] params, PreloginUI preloginUI) {
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
                loggedIn = 1;
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
                loggedIn = 1;

            }
        }
    }

}