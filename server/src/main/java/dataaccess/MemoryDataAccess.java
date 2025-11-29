package dataaccess;


import chess.ChessGame;
import datamodel.UserData;
import datamodel.GameData;


import java.util.ArrayList;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, String> authTokens = new HashMap<>();
    private int gameNumbers = 0;
    private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public boolean saveUser(UserData user) {
        if (getUserData(user.username()) != null) {
            return false;
        }
        users.put(user.username(), user);
        return true;
    }

    @Override
    public UserData getUserData(String username) {
        return users.get(username);
    }

    @Override
    public UserData getUsername(String authToken) {
        String username = authTokens.get(authToken);
        return getUserData(username);
    }

    @Override
    public void registerAuthToken(String authToken, String username) {
        authTokens.put(authToken, username);
    }

    @Override
    public boolean authenticate(String authToken) {
        var res = authTokens.get(authToken);
        return res != null;
    }

    @Override
    public void removeAuthToken(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public void deleteDatabase() {
        users = new HashMap<>();
        authTokens = new HashMap<>();
        games = new HashMap<>();
        gameNumbers = 0;
    }

    @Override
    public int createGame(String gameName) {
        gameNumbers++;
        games.put(gameNumbers, new GameData(gameNumbers, null, null, gameName, null));
        return gameNumbers;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public ArrayList<GameData> gamesList() {
        ArrayList<GameData> listOfGames = new ArrayList<>();
        for (HashMap.Entry<Integer, GameData> entry : games.entrySet()) {
            listOfGames.add(entry.getValue());
        }
        return listOfGames;
    }

    @Override
    public void setWhite(int gameID, String username) {
        GameData game = getGame(gameID);
        GameData updateGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.chessGame());
        games.put(gameID, updateGame);
    }

    @Override
    public void setBlack(int gameID, String username) {
        GameData game = getGame(gameID);
        GameData updateGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.chessGame());
        games.put(gameID, updateGame);
    }

    @Override
    public void updateGame(int gameId, ChessGame chessGame) {
        GameData gameData = games.get(gameId);
        GameData update = new GameData(gameId, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
        games.put(gameId, update);
    }

}
