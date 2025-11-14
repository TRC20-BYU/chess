package server;

import com.google.gson.Gson;
import dataaccess.DBMemoryAccess;
import datamodels.GameData;
import datamodel.GameList;
import datamodel.JoinData;
import datamodel.UserData;
import dataaccess.DataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

import java.util.Map;
import java.util.Objects;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final DataAccess dataAccess;

    public enum PlayerColor {
        WHITE,
        BLACK
    }

    public Server() {

        server = Javalin.create(config -> config.staticFiles.add("web"));

        dataAccess = new DBMemoryAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        // Register your endpoints and exception handlers here.
        server.delete("db", this::deleteDatabase);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.get("game", this::listGames);
        server.exception(ResponseException.class, this::exceptionHandler);
    }


    private void deleteDatabase(Context ctx) throws ResponseException {
        userService.deleteDatabase();
    }

    private void register(Context ctx) throws ResponseException {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new ResponseException(ResponseException.Code.requestError);
        } else {
            var regResult = userService.register(req);
            ctx.result(serializer.toJson(regResult));
        }
    }

    private void login(Context ctx) throws ResponseException {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);
        if (req.username() == null || req.password() == null) {
            throw new ResponseException(ResponseException.Code.requestError);
        } else {
            var regResult = userService.login(req);
            ctx.result(serializer.toJson(regResult));
        }
    }

    private void logout(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        boolean regResult = userService.logout(authToken);
        if (!regResult) {
            throw new ResponseException(ResponseException.Code.authError);
        }
        ctx.result();
    }

    private void createGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, GameData.class);
        if (req.getGameName() == null) {
            throw new ResponseException(ResponseException.Code.requestError);
        } else {
            int result = gameService.createGame(ctx.header("authorization"), req.getGameName());
            ctx.result(new Gson().toJson(Map.of("gameID", result)));
        }
    }

    private void joinGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, JoinData.class);
        if (req.gameID() == 0) {
            throw new ResponseException(ResponseException.Code.requestError);
        } else if (!Objects.equals(req.playerColor(), "WHITE") && !Objects.equals(req.playerColor(), "BLACK")) {
            throw new ResponseException(ResponseException.Code.requestError);
        } else {
            PlayerColor color = PlayerColor.WHITE;
            if (req.playerColor().equals("BLACK")) {
                color = PlayerColor.BLACK;
            }
            gameService.joinGame(ctx.header("authorization"), color, req.gameID());
        }
    }

    private void listGames(Context ctx) throws ResponseException {
        GameList gameList = new GameList(gameService.listGames(ctx.header("authorization")));
        ctx.result(new Gson().toJson(gameList));
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }


    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
