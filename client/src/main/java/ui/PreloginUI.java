package ui;

public class PreloginUI {

    public void help() {
        //Displays text informing the user what actions they can take.
        System.out.println("Register: <username> <password> <email> - registers an account");
        System.out.println("Login: <username> <password> - logs in an account");
        System.out.println("quit - quits the program");
        System.out.println("help - displays possible commands");
    }

    public void login() {
        //http request
        //Prompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.
    }

    public void register() {
        //http request
        //Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.
    }
}
