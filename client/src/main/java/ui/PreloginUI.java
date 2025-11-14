package ui;

import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.UserData;
import serverfacade.ServerFacade;

public class PreloginUI {

    ServerFacade serverFacade;

    public PreloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void help() {
        //Displays text informing the user what actions they can take.
        System.out.println("Register: <username> <password> <email> - registers an account");
        System.out.println("Login: <username> <password> - logs in an account");
        System.out.println("quit - quits the program");
        System.out.println("help - displays possible commands");
    }

    public AuthData login(UserData userData) {
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        System.out.println("Logged in " + authData.username());
        return authData;
    }

    public AuthData register(UserData userData) {
        var responce = serverFacade.post("user", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        System.out.println("Registered " + userData.username());
        return authData;
    }
}
