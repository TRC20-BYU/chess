package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameList;
import datamodel.JoinData;
import datamodel.GameData;
import serverfacade.ServerError;
import serverfacade.ServerFacade;

import java.util.*;

public class PostloginUI {

    ChessGame chess = new ChessGame();
    ServerFacade serverFacade;
    WebSocketUI webSocketUI;
    List<Integer> gameIds = new ArrayList<>();
    String team = "WHITE";

    public PostloginUI(ServerFacade serverFacade, WebSocketUI webSocketUI) {
        this.serverFacade = serverFacade;
        this.webSocketUI = webSocketUI;
        this.webSocketUI.setPostLoginUI(this);
    }

    public void help() {
        //Displays text informing the user what actions they can take.
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "create: " + EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                "<name> " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- creates a game");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "list " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- lists the games");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "join:" + EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                " <ID> [WHITE|BLack] " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- joins the selected game");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "Observe:" + EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                " <ID> " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- observes the selected game");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "logout " +
                EscapeSequences.SET_TEXT_COLOR_BLUE + "- logs out the account");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "quit " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- quits the program");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "help " + EscapeSequences.SET_TEXT_COLOR_BLUE
                + "- displays possible commands" + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void logout(String authToken) {
        serverFacade.delete("session", null, authToken);
    }

    public void createGame(String authToken, String name) {
        GameData gameData = new GameData(0, null, null, name, new ChessGame());
        serverFacade.post("game", gameData, authToken);
        System.out.println("Game created: " + name);
    }

    public void listGame(String authToken) {
        var serializer = new Gson();
        var result = serverFacade.get("game", null, authToken);
        var mapped = serializer.fromJson(result, GameList.class);
        List<GameData> games = mapped.games();
        gameIds = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            gameIds.add(games.get(i).gameID());
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + (i + 1) + " " +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + games.get(i).gameName()
                    + EscapeSequences.SET_TEXT_COLOR_YELLOW + " White player: " + games.get(i).whiteUsername() +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " Black player: " + games.get(i).blackUsername() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    public void joinGame(String authToken, String id, String color) {
        int idnum = 0;
        try {
            idnum = gameIds.get(Integer.parseInt(id) - 1);
        } catch (Exception ex) {
            throw new ServerError("Invalid ID use \"list\" to find valid IDs");
        }
        JoinData joinData = new JoinData(color, idnum);
        String result = serverFacade.put("game", joinData, authToken);
        System.out.println("Game joined!!!");
        team = color;
        webSocketUI.connect(idnum, authToken);
    }

    public void observerGame(String id, String authToken) {
        int idnum;
        try {
            idnum = gameIds.get(Integer.parseInt(id) - 1);
        } catch (Exception ex) {
            throw new ServerError("Invalid ID use \"list\" to find valid IDs");
        }
        webSocketUI.connect(idnum, authToken);
    }

    public void resetTeam() {
        team = "WHITE";
    }

    public void redrawBoard() {
        String board;
        if (Objects.equals(team, "WHITE")) {
            board = printBoard(chess);
            System.out.print(board);
        } else {
            board = rotateboard(chess);
            System.out.print(board);
        }
    }

    public void drawBoard(ChessGame chessGame) {
        chess = chessGame;
        String board;
        if (Objects.equals(team, "WHITE")) {
            board = printBoard(chessGame);
            System.out.print(board);
        } else {
            board = rotateboard(chessGame);
            System.out.print(board);
        }
    }

    private String printBoard(ChessGame chessGame) {
        ChessBoard board = chessGame.getBoard();
        String boardRep = board.toString();
        String boardString = "";
        String header = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "    a  b  c  d  e  f  g  h    " + EscapeSequences.RESET_BG_COLOR;
        String[] lines = boardRep.split("\\R");
        boardString += header + "\n";
        for (int i = 0; i < lines.length; i++) {
            boardString += EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + (8 - i) + " " + EscapeSequences.RESET_BG_COLOR;
            for (int l = 0; l < lines[i].length(); l++) {
                String color = "";
                if ((l + i) % 2 != 0) {
                    color = EscapeSequences.SET_BG_COLOR_BLACK;
                } else {
                    color = EscapeSequences.SET_BG_COLOR_WHITE;
                }
                char symbol = lines[i].charAt(l);
                String piece = chessPieces(String.valueOf(symbol));
                boardString += color + " " + piece + " " + EscapeSequences.RESET_BG_COLOR;
            }
            boardString += EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + (8 - i) + " " + EscapeSequences.RESET_BG_COLOR + "\n";
        }
        boardString += header + "\n";
        return boardString;
    }

    String chessPieces(String piece) {
        if (Objects.equals(piece, ".")) {
            return " ";
        }
        if (Objects.equals(piece, piece.toLowerCase())) {
            return EscapeSequences.SET_TEXT_COLOR_RED + piece + EscapeSequences.RESET_TEXT_COLOR;
        } else {
            return EscapeSequences.SET_TEXT_COLOR_BLUE + piece + EscapeSequences.RESET_TEXT_COLOR;
        }
    }

    private String rotateboard(ChessGame chessGame) {
        ChessBoard board = chessGame.getBoard();
        String boardRep = flipBoard(board.toString());
        String boardString = "";
        String header = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "    h  g  f  e  d  c  b  a    " + EscapeSequences.RESET_BG_COLOR;
        String[] lines = boardRep.split("\\R");
        boardString += header + "\n";
        for (int i = 0; i < lines.length; i++) {
            boardString += EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + (i + 1) + " " + EscapeSequences.RESET_BG_COLOR;
            for (int l = 0; l < lines[i].length(); l++) {
                String color = "";
                if ((l + i) % 2 != 0) {
                    color = EscapeSequences.SET_BG_COLOR_BLACK;
                } else {
                    color = EscapeSequences.SET_BG_COLOR_WHITE;
                }
                char symbol = lines[i].charAt(l);
                String piece = chessPieces(String.valueOf(symbol));
                boardString += color + " " + piece + " " + EscapeSequences.RESET_BG_COLOR;
            }
            boardString += EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + (i + 1) + " " + EscapeSequences.RESET_BG_COLOR + "\n";
        }
        boardString += header + "\n";
        return boardString;
    }

    private String flipBoard(String board) {
        String flipped = "";
        String[] rows = board.split("\n");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[rows.length - 1 - i];
            String reversedString = "";
            for (int k = row.length() - 1; k >= 0; k--) {
                reversedString += row.charAt(k);
            }
            flipped += reversedString + "\n";
        }
        return flipped;
    }

}
