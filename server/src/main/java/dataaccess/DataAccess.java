package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.List;

public interface DataAccess {

    public boolean saveUser(UserData user);

    public UserData getUserData(String username);

    public UserData getUsername(String authToken);

    public void registerAuthToken(String authToken, String username);

    public boolean authenticate(String authToken);

    public void removeAuthToken(String authToken);

    public void deleteDatabase();

    public int createGame(String gameName);

    public GameData getGame(int gameID);

    public List<GameData> gamesList();

    public void setWhite(int gameID, String username);

    public void setBlack(int gameID, String username);
}
