package server;

import com.google.gson.Gson;
import dataModel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);

        // Register your endpoints and exception handlers here.
        server.delete("db", this::deleteDatabase);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

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
