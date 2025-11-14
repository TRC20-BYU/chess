package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.GameList;
import datamodel.JoinData;
import serverfacade.ServerFacade;

import java.util.*;

public class PostloginUI {

    ServerFacade serverFacade;
    List<Integer> gameIds = new ArrayList<>();

    public PostloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void help() {
        //Displays text informing the user what actions they can take.
        System.out.println("create: <name> - creates a game");
        System.out.println("List - lists the games");
        System.out.println("join: <ID> [WHITE|BLack] - joins the selected game");
        System.out.println("Observe: <ID> - observes the selected game");
        System.out.println("logout - logs out the account");
        System.out.println("quit - quits the program");
        System.out.println("help - displays possible commands");
    }

    public void logout(String authToken) {
        //	Logs out the user. Calls the server logout API to logout the user. After logging out with the server, the client should transition to the Prelogin UI.
        serverFacade.delete("session", null, authToken);
    }

    public void createGame(String authToken, String name) {
        // Allows the user to input a name for the new game. Calls the server create API to create the game. This does not join the player to the created game; it only creates the new game in the server.
        GameData gameData = new GameData(0, null, null, name);
        var result = serverFacade.post("game", gameData, authToken);
        System.out.println("Game created: " + name);
    }

    public void listGame(String authToken) {
        var serializer = new Gson();
        var result = serverFacade.get("game", null, authToken);
        var mapped = serializer.fromJson(result, GameList.class);
        List<GameData> games = mapped.games();
        gameIds = new ArrayList<>();
        for (GameData game : games) {
            gameIds.add(game.getGameID());
            System.out.println(game.getGameID() + " " + game.getGameName());
        }
    }

    public void joinGame(String authToken, String id, String color) {
        // Allows the user to specify which game they want to join and what color they want to play. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to join the user to the game.
        JoinData joinData = new JoinData(color, gameIds.get(Integer.parseInt(id)));
        var result = serverFacade.put("game", joinData, authToken);
        System.out.println("Game joined!!!");
        String board = printBoard();
        if (Objects.equals(color, "WHITE")) {
            System.out.print(board);
        } else {
            System.out.print(flippedBoard(board));
        }
    }

    public void observerGame() {
        // Allows the user to specify which game they want to observe. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Additional functionality will be added in Phase 6.
        String board = printBoard();
        System.out.print(board);
    }

    private String printBoard() {
        ChessGame chessGame = new ChessGame();
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

    private String flippedBoard(String board) {
        String flipped = "";
        String[] rows = board.split("\n");
        for (int i = 0; i < rows.length; i++) {
            flipped += rows[rows.length - 1 - i] + "\n";
        }
        return flipped;
    }

}
