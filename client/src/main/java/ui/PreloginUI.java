package ui;

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

    public boolean login(UserData userData) {
        //http request
        //Prompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.
        serverFacade.post("session", userData, null);
        System.out.println("Logged in " + userData.username());
        return true;
    }

    public boolean register(UserData userData) {
        //http request
        //Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.
        serverFacade.post("user", userData, null);
        System.out.println("Registered " + userData.username());
        return true;
    }
}
