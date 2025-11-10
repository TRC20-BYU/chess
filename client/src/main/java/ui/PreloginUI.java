package ui;

public class PreloginUI {

    public void help() {
        //Displays text informing the user what actions they can take.
        System.out.println("Register: <username> <password> <email>");
        System.out.println("Login: <username> <password>");
        System.out.println("quit");
        System.out.println("help");
    }

    void quit() {
        //Exits the program.
    }

    void login() {
        //http request
        //Prompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.
    }

    void register() {
        //http request
        //Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.
    }
}
