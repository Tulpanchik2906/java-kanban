package test.manager.tasks;

import main.manager.tasks.FileBackedTasksManager;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TestDataUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTasksManagerTest extends TasksManagerTest {

    private Path pathTaskManager;

    private FileBackedTasksManager taskManager;

    @BeforeEach
    public void beforeEach() throws IOException {
        pathTaskManager = Paths.get("managerState.txt");

        taskManager = new FileBackedTasksManager(pathTaskManager);
        setTaskManager(taskManager);
    }

    @AfterEach
    public void afterEach() throws IOException {
        // Удаление файла после теста
        Files.delete(pathTaskManager);
    }

    /* Тесты на метод loadFromFile */
    @Test
    public void testLoadFromFileNoTasks() throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(pathTaskManager.toFile(), StandardCharsets.UTF_8))) {
            bufferedWriter.append("id,type,name,status,description,duration,startTime,epic");
            bufferedWriter.newLine();
        }
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.load(pathTaskManager.toFile());
        List<Task> list = tasksManagerFromFile.getTasks();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void testLoadFromFileGetTasks() throws IOException {
        TestDataUtil.addDefault3Task(getTaskManager());
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.load(pathTaskManager.toFile());
        List<Task> list = tasksManagerFromFile.getTasks();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testLoadFromFileGetEpics() throws IOException {
        TestDataUtil.addDefault3Epics(getTaskManager());
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.load(pathTaskManager.toFile());
        List<Epic> list = tasksManagerFromFile.getEpics();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testLoadFromFileGetEpicWithOutSubTasks() throws IOException {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        getTaskManager().addEpic(epic);
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.load(pathTaskManager.toFile());
        List<Epic> list = tasksManagerFromFile.getEpics();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void testLoadFromFileGetSubTasks() throws IOException {
        TestDataUtil.addDefault1EpicWith3SubTask(getTaskManager());
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.load(pathTaskManager.toFile());
        List<SubTask> list = tasksManagerFromFile.getSubTasks();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testLoadFromFileEmptyHistory() throws IOException {
        TestDataUtil.addDefault3Task(getTaskManager());
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.load(pathTaskManager.toFile());
        List<Task> list = tasksManagerFromFile.getHistory();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void testLoadFromFileGetHistory() throws IOException {
        TestDataUtil.addDefault3Task(getTaskManager());
        List<Task> list = getTaskManager().getHistory();
        for (Task task : list) {
            getTaskManager().getTask(task.getId());
        }
        FileBackedTasksManager tasksManagerFromFile
                = FileBackedTasksManager.load(pathTaskManager.toFile());

        List<Task> history = tasksManagerFromFile.getTasks();
        Assertions.assertEquals(3, history.size());
    }

}
