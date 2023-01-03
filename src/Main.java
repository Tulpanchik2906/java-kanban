import manager.Managers;
import manager.tasks.FileBackedTasksManager;
import manager.tasks.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        testFourPracticum();
        //testThreePracticum();
        //testSecondPracticum();
        //testFirstPracticum();
    }

    public static void testFirstPracticum() {
        TaskManager taskManager = Managers.getDefault();

        // Добавление одного эпика с двумя задачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        // Добавление одного эпика с одной задачей
        Epic epic2 = new Epic("Epic2", "Describe Epic2", Status.NEW);
        taskManager.addEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask1", "SubTask1 By Epic2", Status.NEW, epic2.getId());
        taskManager.addSubTask(subTask3);

        printTestInfo(epic1, taskManager);
        printTestInfo(epic2, taskManager);

        // Проверка изменения статуса эпика на Done
        System.out.println("Статус эпика до изменения всех задач а Done: " + epic2.getStatus());
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask3);
        System.out.println("Статус эпика noсле изменения всех задач а Done: " + epic2.getStatus());

        // Проверка удаления всех подзадач с удалением эпика
        taskManager.removeEpicById(epic1.getId());
        System.out.println("Подзадача 1 после удаления эпика: " + taskManager.getTask(subTask1.getId()));
        System.out.println("Подзадача 2 после удаления эпика: " + taskManager.getTask(subTask2.getId()));
    }

    public static void testSecondPracticum() {
        TaskManager taskManager = Managers.getDefault();
        // Добавление одного эпика с двумя задачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        // Добавление одного эпика с одной задачей
        Epic epic2 = new Epic("Epic2", "Describe Epic2", Status.NEW);
        taskManager.addEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask1", "SubTask1 By Epic2", Status.NEW, epic2.getId());
        taskManager.addSubTask(subTask3);

        // Добавление задачи типа Task
        Task task1 = new Task("Task1", "aldksalk", Status.IN_PROGRESS);
        taskManager.addTask(task1);

        taskManager.getEpic(epic1.getId());
        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getTask(task1.getId());

        printHistory(taskManager.getHistory());
    }

    public static void testThreePracticum() {
        TaskManager taskManager = Managers.getDefault();
        // Добавление двух задач типа Task
        Task task1 = new Task("Task_1", "Describe Task_1", Status.NEW);
        Task task2 = new Task("Task_2", "Describe Task_2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Добавление одного эпика с тремя подзадачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", Status.IN_PROGRESS, epic1.getId());
        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 By Epic1", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        printHistory(taskManager.getHistory());

        // Тестирование повтора задачи типа Task
        System.out.println("Тестирование повтора задачи типа Task.");
        System.out.println("В порядке 2 - 1:");
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        printHistory(taskManager.getHistory());
        System.out.println("В порядке 1 - 2:");
        taskManager.getTask(task1.getId());
        printHistory(taskManager.getHistory());

        // Тестирование повтора задачи типа Epic
        System.out.println("Тестирование повтора задачи типа Epic.");
        System.out.println("Один раз вызвали эпик:");
        taskManager.getEpic(epic1.getId());
        printHistory(taskManager.getHistory());
        System.out.println("Два раза вызвали эпик:");
        taskManager.getEpic(epic1.getId());
        printHistory(taskManager.getHistory());

        // Тестирование повтора задачи типа SubTask
        System.out.println("Тестирование повтора задачи типа SubTask.");
        System.out.println("Один раз вызвали подзадачу:");
        taskManager.getSubTask(subTask1.getId());
        printHistory(taskManager.getHistory());
        System.out.println("Два раза вызвали подзадачу:");
        taskManager.getSubTask(subTask1.getId());
        printHistory(taskManager.getHistory());

        //Проверка удаления одной задачи типа Task
        System.out.println("Проверка удаления одной задачи типа Task.");
        taskManager.removeTaskById(task1.getId());
        printHistory(taskManager.getHistory());

        //Проверка удаления одной задачи типа SubTask
        System.out.println("Проверка удаления одной задачи типа SubTask");
        taskManager.removeSubTaskById(subTask1.getId());
        printHistory(taskManager.getHistory());

        //Проверка удаления одной задачи типа Epic
        System.out.println("Проверка удаления одной задачи типа Epic");
        taskManager.removeEpicById(epic1.getId());
        printHistory(taskManager.getHistory());


    }

    public static void testFourPracticum() throws IOException {
        Path path = Paths.get("managerState.txt");
        FileBackedTasksManager taskManager = new FileBackedTasksManager(path);
        // Добавление двух задач типа Task
        Task task1 = new Task("Task_1", "Describe Task_1", Status.NEW);
        Task task2 = new Task("Task_2", "Describe Task_2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Добавление одного эпика с тремя подзадачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", Status.IN_PROGRESS, epic1.getId());
        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 By Epic1", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        System.out.println("После добавления 2-х задач типа Task и одного Epic с 3-мя SubTask:");
        printHistory(taskManager.getHistory());

        // восстановление менеджера
        System.out.println("Состояние после восстановления:");
        FileBackedTasksManager tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        System.out.println("Тестирование повтора задачи типа Task.");
        System.out.println("В порядке 2 - 1:");
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        printHistory(taskManager.getHistory());
        System.out.println("В порядке 1 - 2:");
        taskManager.getTask(task1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        // Тестирование повтора задачи типа Epic
        System.out.println("Тестирование повтора задачи типа Epic.");
        System.out.println("Один раз вызвали эпик:");
        taskManager.getEpic(epic1.getId());
        printHistory(taskManager.getHistory());
        System.out.println("Два раза вызвали эпик:");
        taskManager.getEpic(epic1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        // Тестирование повтора задачи типа SubTask
        System.out.println("Тестирование повтора задачи типа SubTask.");
        System.out.println("Один раз вызвали подзадачу:");
        taskManager.getSubTask(subTask1.getId());
        printHistory(taskManager.getHistory());
        System.out.println("Два раза вызвали подзадачу:");
        taskManager.getSubTask(subTask1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        //Проверка удаления одной задачи типа Task
        System.out.println("Проверка удаления одной задачи типа Task.");
        taskManager.removeTaskById(task1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        //Проверка удаления одной задачи типа SubTask
        System.out.println("Проверка удаления одной задачи типа SubTask");
        taskManager.removeSubTaskById(subTask1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        //Проверка удаления одной задачи типа Epic
        System.out.println("Проверка удаления одной задачи типа Epic");
        taskManager.removeEpicById(epic1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

        // Проверка добавления задачи типа Task после удаления
        System.out.println("Проверка добавления задачи типа Task после удаления:");
        taskManager.addTask(task1);
        taskManager.getTask(task1.getId());
        printHistory(taskManager.getHistory());

        System.out.println("Состояние после восстановления:");
        tasksManagerFromFile = FileBackedTasksManager.loadFromFile(path.toFile());
        printHistory(tasksManagerFromFile.getHistory());

    }


    public static void printTestInfo(Epic epic, TaskManager taskManager) {
        System.out.println("Информация об эпике:");
        System.out.println(epic);
        System.out.println("Информация о подзадачах:");
        for (SubTask subTask : taskManager.getAllSubTaskByEpicId(epic.getId())) {
            System.out.println(subTask);
        }
    }

    public static void printHistory(List history) {
        System.out.println("История просмотров задач:");
        for (int i = history.size() - 1; i >= 0; i--) {
            System.out.println((history.size() - i) + " " + history.get(i));
        }
    }
}
