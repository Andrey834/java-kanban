package main.managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String token;
    private final String uri;
    private final HttpRequest register;

    public KVTaskClient(String uri) throws IOException, InterruptedException {
        this.uri = uri;
        register = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "register"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        token = client.send(register, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "load/" + key + "?API_TOKEN=" + token))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException | IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(uri + "save/" + key + "?API_TOKEN=" + token))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException | IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
