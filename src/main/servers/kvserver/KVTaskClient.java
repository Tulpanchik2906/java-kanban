package main.servers.kvserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private HttpClient httpClient;
    private HttpResponse.BodyHandler<String> handler;
    private String url;
    public KVTaskClient(String url){
        httpClient = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        this.url = url;
    }
    public String getApiToken() throws IOException, InterruptedException {
        URI uri = URI.create(url+"/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();


        HttpResponse<String> response = httpClient.send(request, handler);

        return response.body();
    }

    public void save(String apiToken, String key, String body) throws IOException, InterruptedException {
        URI uri = URI.create(url+"/save/"+key+"?API_TOKEN="+apiToken);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .build();


        HttpResponse<String> response = httpClient.send(request, handler);

    }

    public String  load(String apiToken, String key) throws IOException, InterruptedException {
        URI uri = URI.create(url+"/load/"+key+"?API_TOKEN="+apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();


        HttpResponse<String> response = httpClient.send(request, handler);

        return response.body();

    }
}
