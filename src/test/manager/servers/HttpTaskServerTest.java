package test.manager.servers;

import com.google.gson.Gson;
import main.servers.httptaskserver.HttpTaskServer;
import main.servers.kvserver.KVServer;
import main.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TestDataUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private Gson gson;
    private URI urlKvServer;
    private HttpClient client;
    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        urlKvServer = URI.create("http://localhost:8078");
        kvServer = new KVServer();
        kvServer.start();
        gson = new Gson();
        client = HttpClient.newHttpClient();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() throws IOException, InterruptedException {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        HttpResponse<String> response = createTask(json);
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        HttpResponse<String> response = createTask(json);

        Task updateTask = newTask;
        updateTask.setName("Lala");
        updateTask.setId(1);

        json = gson.toJson(updateTask);
        response = updateTask(json);

        Assertions.assertEquals(200, response.statusCode());
    }

    private HttpResponse<String> createTask(String json) throws IOException, InterruptedException {
        URI urlHttpTaskServer = URI.create("http://localhost:8080/tasks/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(urlHttpTaskServer).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> updateTask(String json) throws IOException, InterruptedException {
        URI urlHttpTaskServer = URI.create("http://localhost:8080/tasks/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(urlHttpTaskServer).PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}


