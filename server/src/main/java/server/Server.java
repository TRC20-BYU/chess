package server;

import com.google.gson.Gson;
import dataModel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

import java.util.Map;
//import json;

public class Server {

    private final Javalin server;
    private UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);

        var regResult = userService.register(req);


        ctx.result(serializer.toJson(regResult));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
