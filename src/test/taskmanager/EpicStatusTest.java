package test.taskmanager;

import main.manager.Managers;
import main.manager.tasks.TaskManager;
import main.tasks.Epic;
import main.tasks.Status;
import main.tasks.SubTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EpicStatusTest {

    private TaskManager taskManager;
    private Epic epic;
    private Enum status;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic1", "Describe Epic1", Status.NEW);
        taskManager.addEpic(epic);
    }

    // Тест 1: У эпика с пустым списком подзадач статус NEW.
    // Проверка пустого списка подзадач
    @Test
    public void testEpicWithEmptyListSubtasksGetNewStatus() {
        status = taskManager.getEpic(epic.getId()).getStatus();
        Assertions.assertEquals(Status.NEW, status);
    }

    // Тест 2: У эпика со списком подзадач из 3 новых подзадач статус будет NEW.
    // Проверка, что если все подзадачи New, то и статус эпика New
    @Test
    public void testEpicWith3NewSubtasksGetNewStatus() {
        add3SubtasksWithOneStatus(Status.NEW);
        status = taskManager.getEpic(epic.getId()).getStatus();
        Assertions.assertEquals(Status.NEW, status);
    }

    // Тест 3: У эпика со списком подзадач из 3 завершенных подзадач статус будет DONE
    // Проверка, что если все подзадачи Done, то и статус эпика Done.
    @Test
    public void testEpicWith3DoneSubtasksGetDoneStatus() {
        add3SubtasksWithOneStatus(Status.DONE);
        status = taskManager.getEpic(epic.getId()).getStatus();
        Assertions.assertEquals(Status.DONE, status);
    }

    // Тест 4: У эпика со списком подзадач из 3 завершенных подзадач и у 3 новой статус будет IN_PROGRESS.
    // Проверка, что если в списке подзадач есть и новые, и завершенные, то статус эпика InProgress
    @Test
    public void testEpicWith3DoneAnd3NewSubtasksGetInProgressStatus() {
        add3SubtasksWithOneStatus(Status.DONE);
        add3SubtasksWithOneStatus(Status.NEW);
        status = taskManager.getEpic(epic.getId()).getStatus();
        Assertions.assertEquals(Status.IN_PROGRESS, status);
    }

    // Тест 5: У эпика со списком подзадач из 3 подзадач в статусе InProgress статус будет InProgress
    // Проверка, что если все подзадачи Done, то и статус эпика Done.
    @Test
    public void testEpicWith3InProgressSubtasksGetInProgressStatus() {
        add3SubtasksWithOneStatus(Status.IN_PROGRESS);
        status = taskManager.getEpic(epic.getId()).getStatus();
        Assertions.assertEquals(Status.IN_PROGRESS, status);
    }


    private void add3SubtasksWithOneStatus(Status status) {
        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 By Epic1", status, epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 By Epic1", status, epic.getId());
        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 By Epic1", status, epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
    }
}
