package test.manager.tasks;

import main.manager.tasks.HttpTaskManager;
import main.manager.tasks.TaskManager;
import main.servers.kvserver.KVServer;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TestDataUtil;

import java.io.IOException;
import java.util.List;

public class HttpTaskManagerTest extends TasksManagerTest {

    private KVServer kvServer;
    private TaskManager taskManager;
    private String url = "http://localhost:8080";

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(url);
        setTaskManager(taskManager);

    }

    @AfterEach
    public void afterEach() throws IOException {
        kvServer.stop();
    }


    /* Тесты на метод load */
    @Test
    public void testLoadNoTasks() throws IOException, InterruptedException {
        TestDataUtil.addDefault3Task(getTaskManager());
        getTaskManager().cleanTasks();
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<Task> list = httpTaskManager.getTasks();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void testLoadFromFileGetTasks() throws IOException, InterruptedException {
        TestDataUtil.addDefault3Task(getTaskManager());
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<Task> list = httpTaskManager.getTasks();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testLoadFromFileGetEpics() throws IOException, InterruptedException {
        TestDataUtil.addDefault3Epics(getTaskManager());
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<Epic> list = httpTaskManager.getEpics();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testLoadFromFileGetEpicWithOutSubTasks() throws IOException, InterruptedException {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        getTaskManager().addEpic(epic);
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<Epic> list = httpTaskManager.getEpics();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void testLoadFromFileGetSubTasks() throws IOException, InterruptedException {
        TestDataUtil.addDefault1EpicWith3SubTask(getTaskManager());
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<SubTask> list = httpTaskManager.getSubTasks();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testLoadEmptyHistory() throws IOException, InterruptedException {
        TestDataUtil.addDefault3Task(getTaskManager());
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<Task> list = httpTaskManager.getHistory();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void testLoadFromFileGetHistory() throws IOException, InterruptedException {
        TestDataUtil.addDefault3Task(getTaskManager());
        List<Task> list = getTaskManager().getTasks();
        for (Task task : list) {
            getTaskManager().getTask(task.getId());
        }
        HttpTaskManager httpTaskManager = HttpTaskManager.loadHttpTaskManager(url);
        List<Task> history = httpTaskManager.getHistory();
        Assertions.assertEquals(3, history.size());
    }
}
