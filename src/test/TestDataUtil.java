package test;

import main.manager.tasks.TaskManager;
import main.tasks.Epic;
import main.tasks.Status;
import main.tasks.SubTask;
import main.tasks.Task;

public class TestDataUtil {
    public static Task createDefaultTask() {
        return new Task("Task_1", "Describe Task_1", Status.NEW);
    }

    public static Epic createDefaultEpicWithOutSubtasks() {
        return new Epic("Epic1", "Describe Epic1", Status.NEW);
    }

    public static SubTask createDefaultSubTaskWithEpic(TaskManager taskManager) {
        Epic epic = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic);
        return new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic.getId());
    }

    public static void addDefault3Task(TaskManager taskManager) {
        Task task1 = new Task("Task_1", "Describe Task_1", Status.NEW);
        Task task2 = new Task("Task_2", "Describe Task_2", Status.NEW);
        Task task3 = new Task("Task_3", "Describe Task_3", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
    }

    public static void addDefault1EpicWith3SubTask(TaskManager taskManager) {
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", Status.IN_PROGRESS, epic1.getId());
        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 By Epic1", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
    }

    public static void addDefault3Epics(TaskManager taskManager) {
        addDefault1EpicWith3SubTask(taskManager);
        Epic epic2 = new Epic("Epic2", "Describe Epic2", Status.NEW);
        Epic epic3 = new Epic("Epic3", "Describe Epic3", Status.NEW);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
    }

    public static void add3InProgressSubtasksByEpic(Epic epic, TaskManager taskManager){
        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.IN_PROGRESS, epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", Status.IN_PROGRESS, epic.getId());
        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 By Epic1", Status.IN_PROGRESS, epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
    }
}
