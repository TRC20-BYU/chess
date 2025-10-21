package server;

import com.google.gson.Gson;
import dataModel.RegistrationResult;
import dataModel.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import passoff.exception.ResponseParseException;
import service.UserService;
import server.ResponseException;

import java.util.Map;
//import json;

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
        if (req.username() == null && req.password() == null && req.email() == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("Error: bad request");
        } else {
            var regResult = userService.register(req);
            if (regResult == null) {
                ctx.status(HttpStatus.UNAUTHORIZED);
                ctx.result("Error: unauthorized");
            } else {
                ctx.result(serializer.toJson(regResult));
            }
        }
    }

    private void login(Context ctx) throws ResponseException {
        System.out.println("here1");
        var serializer = new Gson();
        System.out.println("here2");
        String reqJson = ctx.body();
        System.out.println("here3");
        var req = serializer.fromJson(reqJson, UserData.class);
        System.out.println("here4");
        if (req.username() == null && req.password() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: no dogs with fleas");
        } else {
            var regResult = userService.login(req);
            if (regResult == null) {
                ctx.result("Error: unauthorized");
            } else {
                ctx.result(serializer.toJson(regResult));
            }
        }
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, RegistrationResult.class);
        boolean regResult = userService.logout(req.authToken());
        if (!regResult) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            ctx.result("Error: unauthorized");
        }
    }


    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
