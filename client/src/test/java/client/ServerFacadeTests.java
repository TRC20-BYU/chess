package client;

import com.google.gson.Gson;
import datamodel.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerError;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {

        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String portLabel = Integer.toString(port);
        serverFacade = new ServerFacade(portLabel);
        serverFacade.delete("db", null, null);
    }

    @BeforeEach
    public void start() {
        serverFacade.delete("db", null, null);
    }

    @AfterAll
    static void stopServer() {
        serverFacade.delete("db", null, null);
        server.stop();
    }


    @Test
    public void registerGood() {
        UserData userData = new UserData("joe", "joe", "joe");
        var responce = serverFacade.post("user", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        Assertions.assertNotNull(authData);
    }

    @Test
    public void registerBad() {
        UserData userData = new UserData("joe", "joe", "joe");
        serverFacade.post("user", userData, null);
        Assertions.assertThrows(ServerError.class, () -> serverFacade.post("user", userData, null));

    }

    @Test
    public void loginGood() {
        UserData userData = new UserData("joe", "joe", "joe");
        serverFacade.post("user", userData, null);
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        Assertions.assertNotNull(authData);
    }

    @Test
    public void loginBad() {
        UserData userData = new UserData("joe", "joe", "joe");
        Assertions.assertThrows(ServerError.class, () -> serverFacade.post("session", userData, null));
    }

    @Test
    public void logoutGood() {
        UserData userData = new UserData("joe", "joe", "joe");
        serverFacade.post("user", userData, null);
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        Assertions.assertDoesNotThrow(() -> serverFacade.delete("session", userData, authData.authToken()));
    }

    @Test
    public void logoutBad() {
        UserData userData = new UserData("joe", "joe", "joe");
        Assertions.assertThrows(ServerError.class, () -> serverFacade.delete("session", userData, null));
    }

    @Test
    public void createGood() {
        UserData userData = new UserData("joe", "joe", "joe");
        serverFacade.post("user", userData, null);
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        GameData gameData = new GameData(0, null, null, "something");
        Assertions.assertDoesNotThrow(() -> serverFacade.post("game", gameData, authData.authToken()));
    }

    @Test
    public void createBad() {
        GameData gameData = new GameData(0, null, null, "something");
        Assertions.assertThrows(ServerError.class, () -> serverFacade.post("game", gameData, null));
    }

    @Test
    public void joinGood() {
        UserData userData = new UserData("joe", "joe", "joe");
        serverFacade.post("user", userData, null);
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        GameData gameData = new GameData(0, null, null, "something");
        serverFacade.post("game", gameData, authData.authToken());
        JoinData joinData = new JoinData("WHITE", 1);
        Assertions.assertDoesNotThrow(() -> serverFacade.put("game", joinData, authData.authToken()));
    }

    @Test
    public void joinBad() {
        JoinData joinData = new JoinData("WHITE", 0);
        Assertions.assertThrows(ServerError.class, () -> serverFacade.post("game", joinData, null));
    }


    @Test
    public void listGood() {
        UserData userData = new UserData("joe", "joe", "joe");
        serverFacade.post("user", userData, null);
        String responce = serverFacade.post("session", userData, null);
        var serializer = new Gson();
        AuthData authData = serializer.fromJson(responce, AuthData.class);
        GameData gameData = new GameData(0, null, null, "something");
        serverFacade.post("game", gameData, authData.authToken());
        var games = serverFacade.get("game", null, authData.authToken());
        var mapped = serializer.fromJson(games, GameList.class);
        Assertions.assertNotNull(mapped);
    }

    @Test
    public void listBad() {
        Assertions.assertThrows(ServerError.class, () -> serverFacade.get("game", null, null));
    }

}
