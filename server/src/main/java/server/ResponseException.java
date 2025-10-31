package server;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        requestError,
        authError,
        takenError,
        serverError,
    }

    final private Code code;
    final private String body;

    public ResponseException(Code code) {
        this.code = code;
        body = "";
    }

    public ResponseException(Code code, String body) {
        this.code = code;
        this.body = body;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", codeMessage()));
    }

    private String codeMessage() {
        return switch (code) {
            case requestError -> "Error: bad request";
            case authError -> "Error: unauthorized";
            case takenError -> "Error: already taken";
            case serverError -> "Error: " + " ";
            default -> "unknown";
        };
    }

    int toHttpStatusCode() {
        return switch (code) {
            case requestError -> 400;
            case authError -> 401;
            case takenError -> 403;
            case serverError -> 500;
        };
    }
}
