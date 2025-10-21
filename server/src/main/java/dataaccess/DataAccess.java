package dataaccess;

import dataModel.UserData;

public interface DataAccess {

    public boolean saveUser(UserData user);

    public UserData getUser(String username);

    public void registerAuthToken(String authToken, String username);

    public boolean authenticate(String authToken);

    public void removeAuthToken(String authToken);

    public void deleteDatabase();

    public int createGame(String gameName);

}
