package test.manager.servers;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
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
    private HttpClient client;

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        gson = new Gson();
        client = HttpClient.newHttpClient();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() throws IOException {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        HttpResponse<String> response = createTask(json);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertTrue(response.body().contains("id"));
    }

    @Test
    public void testErrorCreateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        HttpResponse<String> response = createTask(json);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        newTask.setId(id);
        json = gson.toJson(newTask);
        response = createTask(json);

        Assertions.assertEquals(500, response.statusCode());
        Assertions.assertEquals("Задача с id=" + id + " уже существует, добавление не произошло.", response.body());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        HttpResponse<String> response = createTask(json);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        newTask.setName("Lala");
        newTask.setId(id);

        json = gson.toJson(newTask);
        response = updateTask(json);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача типа Task успешно изменена.", response.body());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        HttpResponse<String> response = createTask(json);
        int id1 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json);
        int id2 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json);
        int id3 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("[{\"name\":\"Task_1\"," + "\"description\":\"Describe Task_1\",\"id\":" + id1 + "," + "\"status\":\"NEW\",\"duration\":0}," + "{\"name\":\"Task_1\",\"description\":\"Describe Task_1\"," + "\"id\":" + id2 + ",\"status\":\"NEW\",\"duration\":0}," + "{\"name\":\"Task_1\",\"description\":\"Describe Task_1\"," + "\"id\":" + id3 + ",\"status\":\"NEW\",\"duration\":0}]", response.body());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        HttpResponse<String> response = createTask(json);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("{\"name\":\"Task_1\",\"description\":\"Describe Task_1\"," + "\"id\":" + id + ",\"status\":\"NEW\",\"duration\":0}", response.body());
    }

    @Test
    public void testGetNoExistTaskById() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        HttpResponse<String> response = createTask(json);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        deleteAllTask();

        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("null", response.body());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        HttpResponse<String> response = createTask(json);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("Задача типа TASK c id: " + id + " успешно удалена", response.body());
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        createTask(json);
        createTask(json);
        createTask(json);

        HttpResponse<String> response = deleteAllTask();

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), "Все задачи типа Task удалены.");
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

    private HttpResponse<String> deleteAllTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}


