package dataaccess;

import datamodels.GameData;
import datamodel.UserData;
import server.ResponseException;

import java.util.List;

public interface DataAccess {

    public boolean saveUser(UserData user) throws ResponseException;

    public UserData getUserData(String username) throws ResponseException;

    public UserData getUsername(String authToken) throws ResponseException;

    public void registerAuthToken(String authToken, String username) throws ResponseException;

    public boolean authenticate(String authToken) throws ResponseException;

    public void removeAuthToken(String authToken) throws ResponseException;

    public void deleteDatabase() throws ResponseException;

    public int createGame(String gameName) throws ResponseException;

    public GameData getGame(int gameID);

    public List<GameData> gamesList();

    public void setWhite(int gameID, String username) throws ResponseException;

    public void setBlack(int gameID, String username) throws ResponseException;
}
