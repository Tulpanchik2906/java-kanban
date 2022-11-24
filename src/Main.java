import manager.Managers;
import manager.tasks.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        testSecondPracticum();
    }

    public static void testFirstPracticum(){
        TaskManager taskManager = Managers.getDefault();

        // Добавление одного эпика с двумя задачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1",  Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        // Добавление одного эпика с одной задачей
        Epic epic2 = new Epic("Epic2", "Describe Epic2",  Status.NEW);
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

    public static void testSecondPracticum(){
        TaskManager taskManager = Managers.getDefault();
        // Добавление одного эпика с двумя задачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1",  Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        // Добавление одного эпика с одной задачей
        Epic epic2 = new Epic("Epic2", "Describe Epic2",  Status.NEW);
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

    public static void printTestInfo(Epic epic, TaskManager taskManager) {
        System.out.println("Информация об эпике:");
        System.out.println(epic);
        System.out.println("Информация о подзадачах:");
        for (SubTask subTask : taskManager.getAllSubTaskByEpicId(epic.getId())) {
            System.out.println(subTask);
        }
    }

    public static void printHistory(List history){
        System.out.println("История просмотров задач:");
        for (int i = history.size()-1; i>=0;i--){
            System.out.println((history.size() -i) + " " + history.get(i));
        }
    }
}
