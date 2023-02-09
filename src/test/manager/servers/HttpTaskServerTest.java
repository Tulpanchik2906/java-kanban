package test.manager.servers;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import main.servers.httptaskserver.HttpTaskServer;
import main.servers.kvserver.KVServer;
import main.tasks.Epic;
import main.tasks.Status;
import main.tasks.SubTask;
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
import java.time.LocalDateTime;

public class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private Gson gson;
    private HttpClient client;
    private final String urlHttpServer = "http://localhost:8080";

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
    public void afterEach() throws IOException, InterruptedException {
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        deleteAllTask(url);
        url = URI.create(urlHttpServer + "/tasks/task");
        deleteAllTask(url);

        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void testCreateSubTask() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertTrue(response.body().contains("id"));
    }

    @Test
    public void testGetSubTasksByEpicId() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int subtaskId1 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int subtaskId2= (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/subtask/epic/?id="+epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[{\"epicId\":"+epicId+"," +
                "\"name\":\"SubTask1\",\"description\":\"SubTask1 By Epic1\"," +
                        "\"id\":"+subtaskId1+",\"status\":\"NEW\",\"duration\":0}," +
                        "{\"epicId\":"+epicId+",\"name\":\"SubTask1\"," +
                        "\"description\":\"SubTask1 By Epic1\"," +
                        "\"id\":"+subtaskId2+",\"status\":\"NEW\",\"duration\":0}]", response.body());
    }

    @Test
    public void testErrorCreateSubTask() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        subTask.setId(id);
        json = gson.toJson(subTask);
        response = createTask(json, url);

        Assertions.assertEquals(500, response.statusCode());
        Assertions.assertEquals("Подзадача с id=" + id + " уже существует, добавление не произошло.", response.body());
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        subTask.setName("Lala");
        subTask.setId(id);

        json = gson.toJson(subTask);
        response = updateTask(json, url);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача типа SubTask успешно изменена.", response.body());
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int id1 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int id2 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int id3 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("[{\"epicId\":"+epicId+",\"name\":\"SubTask1\"," +
                "\"description\":\"SubTask1 By Epic1\",\"id\":" + id1 + ",\"status\":\"NEW\",\"duration\":0}," +
                "{\"epicId\":"+epicId+",\"name\":\"SubTask1\",\"description\":\"SubTask1 By Epic1\"," +
                "\"id\":" + id2 + ",\"status\":\"NEW\",\"duration\":0},{\"epicId\":"+epicId+"," +
                "\"name\":\"SubTask1\",\"description\":\"SubTask1 By Epic1\"," +
                "\"id\":" + id3 + ",\"status\":\"NEW\",\"duration\":0}]", response.body());
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("{\"epicId\":23,\"name\":\"SubTask1\",\"description\":\"SubTask1 By Epic1\"," +
                "\"id\":" + id + ",\"status\":\"NEW\",\"duration\":0}", response.body());
    }

    @Test
    public void testGetNoExistSubTaskById() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        deleteAllTask(url);

        url = URI.create(urlHttpServer + "/tasks/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("null", response.body());
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("Задача типа Subtask c id: " + id + " успешно удалена", response.body());
    }

    @Test
    public void testDeleteAllSubTasks() throws IOException, InterruptedException {
        int epicId = createNewEpic();
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);

        URI url = URI.create(urlHttpServer + "/tasks/subtask");
        String json = gson.toJson(subTask);

        createTask(json, url);
        createTask(json, url);
        createTask(json, url);

        HttpResponse<String> response = deleteAllTask(url);

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("Все задачи типа SubTask удалены.", response.body());
    }

    @Test
    public void testCreateEpic() throws IOException, InterruptedException {
        Epic newTask = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertTrue(response.body().contains("id"));
    }

    @Test
    public void testErrorCreateEpic() throws IOException, InterruptedException {
        Epic newEpic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newEpic);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        newEpic.setId(id);
        json = gson.toJson(newEpic);
        response = createTask(json, url);

        Assertions.assertEquals(500, response.statusCode());
        Assertions.assertEquals("Эпик с id=" + id + " уже существует, добавление не произошло.", response.body());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic newEpic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newEpic);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        newEpic.setName("Lala");
        newEpic.setId(id);

        json = gson.toJson(newEpic);
        response = updateTask(json, url);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача типа Epic успешно изменена.", response.body());
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(epic);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);
        int id1 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int id2 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int id3 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(isEqualsEpicList(response.body(), id1, id2, id3));
    }

    private String getEpicsList(int id1, int id2, int id3){
        return "[{\"subTaskIds\":[],\"name\":\"Epic1\",\"description\":\"Describe Epic1\"," +
                "\"id\":" + id1 + ",\"status\":\"NEW\",\"duration\":0},{\"subTaskIds\":[],\"name\":\"Epic1\"," +
                "\"description\":\"Describe Epic1\",\"id\":" + id2 + ",\"status\":\"NEW\"," +
                "\"duration\":0},{\"subTaskIds\":[],\"name\":\"Epic1\",\"description\":\"Describe Epic1\"," +
                "\"id\":" + id3 + ",\"status\":\"NEW\",\"duration\":0}]";
    }
    private boolean isEqualsEpicList(String body, int id1, int id2, int id3){
        return getEpicsList(id1, id2, id3).equals(body) ||
                getEpicsList(id1, id3, id2).equals(body) ||
                getEpicsList(id2, id1, id3).equals(body) ||
                getEpicsList(id2, id3, id1).equals(body) ||
                getEpicsList(id3, id2, id1).equals(body) ||
                getEpicsList(id3, id1, id2).equals(body);

    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic newTask = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("{\"subTaskIds\":[],\"name\":\"Epic1\",\"description\":\"Describe Epic1\"," +
                "\"id\":" + id + ",\"status\":\"NEW\",\"duration\":0}", response.body());
    }

    @Test
    public void testGetNoExistEpicById() throws IOException, InterruptedException {
        Epic newTask = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        deleteAllTask(url);

        url = URI.create(urlHttpServer + "/tasks/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("null", response.body());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic newTask = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        HttpResponse<String> response = createTask(json, url);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача типа Epic c id: " + id + " успешно удалена", response.body());
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic newTask = TestDataUtil.createDefaultEpicWithOutSubtasks();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        createTask(json, url);
        createTask(json, url);
        createTask(json, url);

        HttpResponse<String> response = deleteAllTask(url);

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), "Все задачи типа Epic удалены.");
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertTrue(response.body().contains("id"));
    }

    @Test
    public void testErrorCreateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        newTask.setId(id);
        json = gson.toJson(newTask);
        response = createTask(json, url);

        Assertions.assertEquals(500, response.statusCode());
        Assertions.assertEquals("Задача с id=" + id + " уже существует, добавление не произошло.", response.body());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);

        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        newTask.setName("Lala");
        newTask.setId(id);

        json = gson.toJson(newTask);
        response = updateTask(json, url);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача типа Task успешно изменена.", response.body());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        int id1 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int id2 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        response = createTask(json, url);
        int id3 = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("[{\"name\":\"Task_1\","
                + "\"description\":\"Describe Task_1\",\"id\":"
                + id1 + "," + "\"status\":\"NEW\",\"duration\":0},"
                + "{\"name\":\"Task_1\",\"description\":\"Describe Task_1\","
                + "\"id\":" + id2 + ",\"status\":\"NEW\",\"duration\":0},"
                + "{\"name\":\"Task_1\",\"description\":\"Describe Task_1\","
                + "\"id\":" + id3 + ",\"status\":\"NEW\",\"duration\":0}]", response.body());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("{\"name\":\"Task_1\",\"description\":\"Describe Task_1\","
                + "\"id\":" + id + ",\"status\":\"NEW\",\"duration\":0}", response.body());
    }

    @Test
    public void testGetNoExistTaskById() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        deleteAllTask(url);

        url = URI.create(urlHttpServer + "/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("null", response.body());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);

        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        int id = (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals("Задача типа TASK c id: " + id + " успешно удалена", response.body());
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/task");
        createTask(json, url);
        createTask(json, url);
        createTask(json, url);

        HttpResponse<String> response = deleteAllTask(url);

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), "Все задачи типа Task удалены.");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        newTask.setDuration(60);
        newTask.setStartTime(LocalDateTime.parse("2023-01-20T12:00"));


        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        int taskId =(int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        int epicId = createNewEpic();

        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T14:00"));

        url = URI.create(urlHttpServer + "/tasks/subtask");
        json = gson.toJson(subTask);
        response = createTask(json, url);
        int subTaskId = (int) JsonParser.parseString(response.body())
                .getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/" );
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals( "[{\"name\":\"Task_1\",\"description\":\"Describe Task_1\"," +
                "\"id\":"+taskId+",\"status\":\"NEW\",\"duration\":60,\"startTime\":" +
                "{\"date\":{\"year\":2023,\"month\":1,\"day\":20},\"time\":" +
                "{\"hour\":12,\"minute\":0,\"second\":0,\"nano\":0}}}," +
                "{\"epicId\":"+epicId+",\"name\":\"SubTask1\"," +
                "\"description\":\"SubTask1 By Epic1\",\"id\":"+subTaskId+"," +
                "\"status\":\"NEW\",\"duration\":60,\"startTime\":" +
                "{\"date\":{\"year\":2023,\"month\":1,\"day\":20}," +
                "\"time\":{\"hour\":14,\"minute\":0,\"second\":0,\"nano\":0}}}]", response.body());
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create(urlHttpServer + "/tasks/" );
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals( "[]", response.body());
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        URI url = URI.create(urlHttpServer + "/tasks/history" );
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[]", response.body());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task newTask = TestDataUtil.createDefaultTask();
        String json = gson.toJson(newTask);
        URI url = URI.create(urlHttpServer + "/tasks/task");
        HttpResponse<String> response = createTask(json, url);
        int taskId =(int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();

        int epicId = createNewEpic();

        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epicId);
        url = URI.create(urlHttpServer + "/tasks/subtask");
        json = gson.toJson(subTask);
        response = createTask(json, url);
        int subTaskId = (int) JsonParser.parseString(response.body())
                .getAsJsonObject().get("id").getAsLong();

        url = URI.create(urlHttpServer + "/tasks/task/?id="+taskId );
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create(urlHttpServer + "/tasks/epic/?id="+epicId );
        request = HttpRequest.newBuilder().uri(url).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create(urlHttpServer + "/tasks/subtask/?id="+subTaskId );
        request = HttpRequest.newBuilder().uri(url).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create(urlHttpServer + "/tasks/history" );
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[{\"name\":\"Task_1\",\"description\":\"Describe Task_1\"," +
                "\"id\":"+taskId+",\"status\":\"NEW\",\"duration\":0}," +
                "{\"subTaskIds\":["+subTaskId+"],\"name\":\"Epic1\"," +
                "\"description\":\"Describe Epic1\"," +
                "\"id\":"+epicId+",\"status\":\"NEW\",\"duration\":0}," +
                "{\"epicId\":"+epicId+",\"name\":\"SubTask1\",\"description\":" +
                "\"SubTask1 By Epic1\",\"id\":"+subTaskId+",\"status\":\"NEW\",\"duration\":0}]",
                response.body());
    }

    private HttpResponse<String> createTask(String json, URI url)
            throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private HttpResponse<String> updateTask(String json, URI url) throws IOException,
            InterruptedException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteAllTask(URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private int createNewEpic() throws IOException, InterruptedException {
        Epic newEpic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        URI url = URI.create(urlHttpServer + "/tasks/epic");
        String json = gson.toJson(newEpic);
        HttpResponse<String> response = createTask(json, url);

        return (int) JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsLong();
    }
}


