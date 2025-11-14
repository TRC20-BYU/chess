package ui;

import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.GameList;
import datamodel.JoinData;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    }

    public void observerGame() {
        // Allows the user to specify which game they want to observe. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Additional functionality will be added in Phase 6.

    }

    void printBoard() {

    }

}
