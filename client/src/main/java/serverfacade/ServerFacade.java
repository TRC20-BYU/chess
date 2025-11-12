package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();

    public void post(String path, Object body) {
        var request = buildRequest("post", path, body);
        System.out.println(sendRequest(request));
    }

    public void delete(String path, Object body) {
        var request = buildRequest("delete", path, body);
        System.out.println(sendRequest(request));
    }

    public void put(String path, Object body) {
        var request = buildRequest("put", path, body);
        System.out.println(sendRequest(request));
    }

    public void get(String path, Object body) {
        var request = buildRequest("get", path, body);
        System.out.println(sendRequest(request));
    }


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/" + path)).method(method, makeRequestBody(body));
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
}
