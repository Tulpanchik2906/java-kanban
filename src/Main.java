import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // Добавление одного эпика с двумя задачами
        Epic epic1 = new Epic("Epic1", "Describe Epic1", -1, Status.NEW);
        taskManager.addEpic(epic1);
        System.out.println(epic1.getId());
        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1",-1, Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", -1, Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        // Добавление одного эпика с одной задачей
        Epic epic2 = new Epic("Epic2", "Describe Epic2", -1, Status.NEW);
        taskManager.addEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask1", "SubTask1 By Epic2",-1, Status.NEW, epic2.getId());
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

    public static void printTestInfo(Epic epic, TaskManager taskManager) {
        System.out.println("Информация об эпике:");
        System.out.println(epic);
        System.out.println("Информация о подзадачах:");
        for (SubTask subTask : taskManager.getAllSubTaskByEpicId(epic.getId())) {
            System.out.println(subTask);
        }
    }
}
