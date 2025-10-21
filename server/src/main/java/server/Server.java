package server;

import com.google.gson.Gson;
import dataModel.GameData;
import dataModel.JoinData;
import dataModel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.Map;

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

        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        // Register your endpoints and exception handlers here.
        server.delete("db", this::deleteDatabase);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
    }


    private void deleteDatabase(Context ctx) {
        userService.deleteDatabase();
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);
        if (req.username() == null || req.password() == null || req.email() == null) {
            exceptionHandler(new ResponseException(ResponseException.Code.requestError), ctx);
        } else {
            var regResult = userService.register(req);
            if (regResult == null) {
                exceptionHandler(new ResponseException(ResponseException.Code.takenError), ctx);
            } else {
                ctx.result(serializer.toJson(regResult));
            }
        }
    }

    private void login(Context ctx) throws ResponseException {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);
        if (req.username() == null || req.password() == null) {
            exceptionHandler(new ResponseException(ResponseException.Code.requestError), ctx);
        } else {
            var regResult = userService.login(req);
            if (regResult == null) {
                exceptionHandler(new ResponseException(ResponseException.Code.authError), ctx);
            } else {
                ctx.result(serializer.toJson(regResult));
            }
        }
    }

    private void logout(Context ctx) {
        String authToken = ctx.header("authorization");
        boolean regResult = userService.logout(authToken);
        if (!regResult) {
            exceptionHandler(new ResponseException(ResponseException.Code.authError), ctx);
        }
        ctx.result();
    }

    private void createGame(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, GameData.class);
        if (req.gameName() == null) {
            exceptionHandler(new ResponseException(ResponseException.Code.requestError), ctx);
        } else {
            int result = gameService.createGame(ctx.header("authorization"), req.gameName());
            if (result < 0) {
                exceptionHandler(new ResponseException(ResponseException.Code.authError), ctx);
            } else {
                ctx.result(new Gson().toJson(Map.of("gameID", result)));
            }
        }
    }

    private void joinGame(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, JoinData.class);
        if (req.gameID() == 0 || req.playerColor() == null) {
            exceptionHandler(new ResponseException(ResponseException.Code.requestError), ctx);
        } else {
            PlayerColor color = PlayerColor.WHITE;
            if (req.playerColor().equals("BLACK")) {
                color = PlayerColor.BLACK;
            }
            int result = gameService.joinGame(ctx.header("authorization"), color, req.gameID());
            if (result == -1) {
                exceptionHandler(new ResponseException(ResponseException.Code.authError), ctx);
            }
            if (result == -2) {
                exceptionHandler(new ResponseException(ResponseException.Code.takenError), ctx);
            }
        }
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
