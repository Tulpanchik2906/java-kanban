package main.servers.kvserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient httpClient;
    private final HttpResponse.BodyHandler<String> handler;
    private final String url;
    private final String API_TOKEN;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        this.url = url;
        API_TOKEN = getApiToken();
    }

    private String getApiToken() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();


        HttpResponse<String> response = httpClient.send(request, handler);

        return response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + API_TOKEN);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .build();


        httpClient.send(request, handler);

    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();


        HttpResponse<String> response = httpClient.send(request, handler);

        return response.body();

    }
}
