package server;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        requestError,
        authError,
        takenError,
    }

    final private Code code;

    public ResponseException(Code code) {
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", codeMessage()));
    }

    private String codeMessage() {
        return switch (code) {
            case requestError -> "Error: bad request";
            case authError -> "Error: unauthorized";
            case takenError -> "Error: already taken";
            default -> "unknown";
        };
    }

    int toHttpStatusCode() {
        return switch (code) {
            case requestError -> 400;
            case authError -> 401;
            case takenError -> 403;
        };
    }
}
