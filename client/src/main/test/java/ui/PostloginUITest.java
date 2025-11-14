package ui;

import org.junit.jupiter.api.Test;
import serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

class PostloginUITest {

    @Test
    void printBoard() {
        ServerFacade serverFacade = new ServerFacade();
        PostloginUI postloginUI = new PostloginUI(serverFacade);
        postloginUI.printBoard();
    }
}