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
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "Register: " + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " <username> <password> <email> " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- registers an account");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "Login: " + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " <username> <password> " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- logs in an account");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "quit " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- quits the program");
        System.out.println("    " + EscapeSequences.SET_TEXT_COLOR_GREEN + "help " + EscapeSequences.SET_TEXT_COLOR_BLUE + "- displays possible commands" + EscapeSequences.RESET_TEXT_COLOR);
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
