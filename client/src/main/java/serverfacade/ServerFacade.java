package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();

    public String post(String path, Object body, String authToken) {
        var request = buildRequest("post", path, body, authToken);
        var result = sendRequest(request);
        if (result != null) {
            if (result.statusCode() != 200) {
                String error = errorHandling(result.statusCode());
                System.out.println(error);
                return null;
            }
        }
        return result.body();
    }

    public String delete(String path, Object body, String authToken) {
        var request = buildRequest("delete", path, body, authToken);
        var result = sendRequest(request);
        if (result != null) {
            if (result.statusCode() != 200) {
                String error = errorHandling(result.statusCode());
                System.out.println(error);
                return null;
            }
        }
        return result.body();

    }

    public String put(String path, Object body, String authToken) {
        var request = buildRequest("put", path, body, authToken);
        var result = sendRequest(request);
        if (result != null) {
            if (result.statusCode() != 200) {
                String error = errorHandling(result.statusCode());
                System.out.println(error);
                return null;
            }
        }
        return result.body();
    }

    public String get(String path, Object body, String authToken) {
        var request = buildRequest("get", path, body, authToken);
        var result = sendRequest(request);
        if (result != null) {
            if (result.statusCode() != 200) {
                String error = errorHandling(result.statusCode());
                System.out.println(error);
                return null;
            }
        }
        return result.body();
    }


    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/" + path)).method(method, makeRequestBody(body));
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private String errorHandling(int code) {
        return switch (code) {
            case 400 -> "requestError";
            case 401 -> "authError";
            case 403 -> "takenError";
            case 500 -> "serverError";
            default -> null;
        };
    }
}
