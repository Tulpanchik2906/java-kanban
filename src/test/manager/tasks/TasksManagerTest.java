package test.manager.tasks;

import main.manager.tasks.TaskManager;
import main.manager.tasks.exception.TaskValidationException;
import main.tasks.Epic;
import main.tasks.Status;
import main.tasks.SubTask;
import main.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.TestDataUtil;

import java.time.LocalDateTime;
import java.util.List;

/*
    Алина, сил Вам и терпения!
 */
public abstract class TasksManagerTest<T extends TaskManager> {
    private T taskManager;

    public void setTaskManager(T taskManager) {
        this.taskManager = taskManager;
    }

    public T getTaskManager() {
        return taskManager;
    }

    /*
        Тесты на метод public int addTask(Task task);
     */

    // Метод addTask: Тест успешного сохранения задачи c самогенерирующемся id
    @Test
    public void testSuccessDefaultAddTask() {
        Task task = TestDataUtil.createDefaultTask();
        int id = getTaskManager().addTask(task);
        Assertions.assertEquals(task, getTaskManager().getTask(id));
    }

    // Метод addTask: Тест успешного сохранения задачи c указанным id
    @Test
    public void testSuccessWithForeignIdAddTask() {
        Task task = TestDataUtil.createDefaultTask();
        task.setId(2);
        int id = getTaskManager().addTask(task);
        Assertions.assertEquals(task, getTaskManager().getTask(id));
    }

    // Метод addTask: Тест получения ошибки при попытки сохранить null-значение
    @Test
    public void testAddTaskGetErrorWhenAddNullTask() {
        TaskValidationException ex =
                Assertions.assertThrows(TaskValidationException.class, () -> getTaskManager().addTask(null));

        Assertions.assertEquals("Передано null-значение.", ex.getMessage());
    }

    // Метод addTask: Тест получения результат -1, если такой id уже есть в менеджере задач
    @Test
    public void testAddTaskGetMinusOneWhenAddRepeatTask() {
        Task task = TestDataUtil.createDefaultTask();
        task.setId(2);
        getTaskManager().addTask(task);

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addTask(task));

        Assertions.assertEquals("Задача с id=2 уже существует, добавление не произошло.", ex.getMessage());
    }

    @Test
    public void testAddTaskWithNotNullStartTime() {
        Task task = TestDataUtil.createDefaultTask();
        task.setDuration(60);
        task.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addTask(task);
        Task taskActual = getTaskManager().getTask(id);
        Assertions.assertEquals(task, taskActual);
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:01"),
                taskActual.getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T13:01"),
                taskActual.getEndTime());
    }

    // Метод addTask: Проверка добавления двух не перескающихся задач
    // (добавление до начала уже добавленной задачи)
    @Test
    public void testSuccessAddTaskNoIntersectionAddForTask() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(60);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T14:01"));
        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        getTaskManager().addTask(task1);
        int id = getTaskManager().addTask(task2);

        Assertions.assertEquals(task2, getTaskManager().getTask(id));
    }

    // Метод addTask: Проверка добавления двух не перескающихся задач
    // (добавление после конца уже добавленной задачи)
    @Test
    public void testSuccessAddTaskNoIntersectionAddAfterTask() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(60);

        task1.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T14:01"));
        getTaskManager().addTask(task1);
        int id = getTaskManager().addTask(task2);

        Assertions.assertEquals(task2, getTaskManager().getTask(id));
    }

    // Метод addTask: Проверка добавления двух не перескающихся задач
    // (добавление новой задачи между уже созданными задачами)
    @Test
    public void testSuccessAddTaskNoIntersectionAddBetweenTasks() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(58);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T14:01"));

        Task task3 = TestDataUtil.createDefaultTask();
        task3.setDuration(58);
        task3.setStartTime(LocalDateTime.parse("2023-01-20T13:01"));

        getTaskManager().addTask(task1);
        getTaskManager().addTask(task2);
        int id = getTaskManager().addTask(task3);

        Assertions.assertEquals(task3, getTaskManager().getTask(id));
    }

    // Метод addTask: Не добавляются пересекающие задачи
    // (у задачи совпадает время начала с концом другой задачи)
    @Test
    public void testNoAddTaskHasIntersectionEqualStartTimeVsEndTime() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(59);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T13:00"));

        getTaskManager().addTask(task1);
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addTask(task2));

        Assertions.assertEquals("Задача имеет пересечение с другими задачами. Добавление не произошло.",
                ex.getMessage());
    }

    // Метод addTask: Не добавляются пересекающие задачи
    // (у задачи совпадает время конца с временем начала другой задачи)
    @Test
    public void testNoAddTaskHasIntersectionEqualEndTimeVsStartTime() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(60);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T13:00"));

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T12:00"));

        getTaskManager().addTask(task1);
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addTask(task2));

        Assertions.assertEquals("Задача имеет пересечение с другими задачами. Добавление не произошло.",
                ex.getMessage());
    }

    // Метод addTask: Не добавляются пересекающие задачи
    // (у задачи время начала попадает в промежуток другой задачи)
    @Test
    public void testNoAddTaskHasIntersectionEndTime() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(60);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T12:00"));

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T11:30"));

        getTaskManager().addTask(task1);
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addTask(task2));

        Assertions.assertEquals("Задача имеет пересечение с другими задачами. Добавление не произошло.",
                ex.getMessage());
    }

    // Метод addTask: Не добавляются пересекающие задачи
    // (у задачи время начала попадает в промежуток другой задачи)
    @Test
    public void testNoAddTaskHasIntersectionStartTime() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(60);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T12:00"));

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T12:30"));

        getTaskManager().addTask(task1);
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addTask(task2));

        Assertions.assertEquals("Задача имеет пересечение с другими задачами. Добавление не произошло.",
                ex.getMessage());
    }

    // Тест добавления без пересечений разных задач
    @Test
    public void testAddDifferentTaskTypesIntersectionStartTime() {
        Task task = TestDataUtil.createDefaultTask();
        task.setDuration(60);
        task.setStartTime(LocalDateTime.parse("2023-01-20T12:00"));

        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();

        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T14:00"));

        getTaskManager().addTask(task);
        getTaskManager().addEpic(epic);
        getTaskManager().addSubTask(subTask);

        Assertions.assertEquals(1, getTaskManager().getTasks().size());
        Assertions.assertEquals(2, getTaskManager().getEpics().size());
        Assertions.assertEquals(1, getTaskManager().getSubTasks().size());
    }

    /*
       Тесты на метод public int addEpic(Epic epic);
    */
    // Метод addEpic: Тест успешного сохранения эпика c самогенерирующемся id
    @Test
    public void testSuccessAddEpicDefault() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        int id = getTaskManager().addEpic(epic);
        Assertions.assertEquals(epic, getTaskManager().getEpic(id));
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    // Метод addEpic: Тест успешного сохранения эпика c указанным id
    @Test
    public void testSuccessWithForeignIdAddEpic() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        epic.setId(2);
        int id = getTaskManager().addEpic(epic);
        Assertions.assertEquals(epic, getTaskManager().getEpic(id));
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    // Метод addEpic: Тест получения ошибки при попытки сохранить null-значение
    @Test
    public void testAddEpicGetErrorWhenAddNullEpic() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addEpic(null));

        Assertions.assertEquals("Передано null-значение.", ex.getMessage());
    }

    // Метод addEpic: Тест получения результат -1, если такой id в менеджере задач уже есть
    @Test
    public void testAddEpicGetMinusOneWhenAddRepeatEpic() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        epic.setId(2);
        getTaskManager().addEpic(epic);
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addEpic(epic));

        Assertions.assertEquals("Эпик с id=2 уже существует, добавление не произошло.", ex.getMessage());

    }

    // Проверка вычисления startTime и endTime для Epic с 3 заполненными задачами
    @Test
    public void testEpicStartTimeWith3SubTaskWithNotNullStartTime() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        getTaskManager().getSubTask(subTaskIds.get(0)).setStartTime(LocalDateTime.parse("2023-01-20T12:30"));
        getTaskManager().getSubTask(subTaskIds.get(0)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(1)).setStartTime(LocalDateTime.parse("2023-01-20T13:31"));
        getTaskManager().getSubTask(subTaskIds.get(1)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(2)).setStartTime(LocalDateTime.parse("2023-01-20T14:32"));
        getTaskManager().getSubTask(subTaskIds.get(2)).setDuration(60);
        getTaskManager().updateEpic(epic);

        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:30"), epic.getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T15:32"), epic.getEndTime());
    }

    // Метод addEpic: Проверка вычисления startTime для Epic (null-значение)
    @Test
    public void testAddEpicGetNullStartTime() {
        TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        Assertions.assertNull(epic.getStartTime());
        Assertions.assertNull(epic.getEndTime());
    }

    // Проверка вычисления startTime для Epic с 3 заполненными задачами
    @Test
    public void testEpicStartTimeWith2SubTaskWithNotNullAnd1NullStartTime() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        getTaskManager().getSubTask(subTaskIds.get(0)).setStartTime(LocalDateTime.parse("2023-01-20T12:30"));
        getTaskManager().getSubTask(subTaskIds.get(0)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(2)).setStartTime(LocalDateTime.parse("2023-01-20T14:32"));
        getTaskManager().getSubTask(subTaskIds.get(2)).setDuration(60);
        getTaskManager().updateEpic(epic);

        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:30"), epic.getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T15:32"), epic.getEndTime());
    }

    /*
       Тесты на метод public int addSubTask(SubTask task);
    */
    // Метод addSubTask: Тест успешного сохранения подзадачи c самогенерирующемся id
    @Test
    public void testSuccessAddSubTaskDefault() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        int id = getTaskManager().addSubTask(subTask);
        Assertions.assertEquals(subTask, getTaskManager().getSubTask(id));
    }

    // Метод addSubTask: Тест успешного сохранения подзадачи c указанным id
    @Test
    public void testSuccessWithForeignIdAddSubTask() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        subTask.setId(12);
        int id = getTaskManager().addSubTask(subTask);
        Assertions.assertEquals(subTask, getTaskManager().getSubTask(id));
    }

    // Метод addSubTask: Тест получения ошибки при попытки сохранить null-значение
    @Test
    public void testAddSubTaskGetErrorWhenAddNullSubTask() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addSubTask(null));
        Assertions.assertEquals("Передано null-значение.", ex.getMessage());
    }

    // Метод addSubTask: Тест получения результат -1, если такой id в менеджере задач уже есть
    @Test
    public void testAddSubTaskGetMinusOneWhenAddRepeatSubTask() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        subTask.setId(5);
        getTaskManager().addSubTask(subTask);

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addSubTask(subTask));

        Assertions.assertEquals("Подзадача с id=5 уже существует, добавление не произошло.", ex.getMessage());
    }

    // Метод addSubTask: Тест не добавления подзадачи, если в менеджере нет данного эпика
    @Test
    public void testAddSubTaskWithOutEpic() {
        SubTask subTask = new SubTask("SubTask1", "SubTask1 By Epic1", Status.NEW, 2);

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().addSubTask(subTask));

        Assertions.assertEquals("Нет такого эпика, подзадача не добавилась.", ex.getMessage());
    }

    @Test
    public void testAddSubTaskWithNotNullStartTime() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addSubTask(subTask);
        SubTask taskActual = getTaskManager().getSubTask(id);
        Assertions.assertEquals(subTask, taskActual);
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:01"),
                taskActual.getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T13:01"),
                taskActual.getEndTime());
    }

    /*
       Тесты на метод public void cleanTasks();
    */

    @Test
    public void testSuccessCleanTasks3Task() {
        TestDataUtil.addDefault3Task(taskManager);
        getTaskManager().cleanTasks();
        Assertions.assertEquals(taskManager.getTasks().size(), 0);
    }

    @Test
    public void testSuccessCleanTasks0Task() {
        getTaskManager().cleanTasks();
        Assertions.assertEquals(taskManager.getTasks().size(), 0);
    }

    /*
       Тесты на метод public void cleanEpics();
    */
    @Test
    public void testSuccessCleanEpics3Epic() {
        TestDataUtil.addDefault3Epics(taskManager);

        getTaskManager().cleanEpics();
        Assertions.assertEquals(taskManager.getEpics().size(), 0);
        Assertions.assertEquals(taskManager.getSubTasks().size(), 0);
    }

    @Test
    public void testSuccessCleanEpics0Epic() {
        getTaskManager().cleanEpics();
        Assertions.assertEquals(taskManager.getEpics().size(), 0);
        Assertions.assertEquals(taskManager.getSubTasks().size(), 0);
    }

    /*
       Тесты на метод   public void cleanSubTasks();
    */
    @Test
    public void testSuccessCleanSubTasks3SubTask() {
        TestDataUtil.addDefault1EpicWith3SubTask(getTaskManager());

        getTaskManager().cleanSubTasks();
        Assertions.assertEquals(getTaskManager().getEpics().size(), 1);
        Assertions.assertEquals(getTaskManager().getSubTasks().size(), 0);
        Assertions.assertNull(
                getTaskManager().getEpic(getTaskManager().getEpics().get(0).getId()).getStartTime());
        Assertions.assertNull(
                getTaskManager().getEpic(getTaskManager().getEpics().get(0).getId()).getEndTime());
    }

    @Test
    public void testSuccessCleanSubTasks0SubTask() {
        getTaskManager().cleanSubTasks();
        Assertions.assertEquals(taskManager.getSubTasks().size(), 0);
    }
    /*
       Тесты на метод public List<Task> getTasks();
    */

    @Test
    public void testSuccessGetTasksEmptyList() {
        List<Task> tasks = getTaskManager().getTasks();
        Assertions.assertNotNull(tasks);
        Assertions.assertTrue(tasks.isEmpty());
    }

    @Test
    public void testSuccessGetTasksListWith3Tasks() {
        TestDataUtil.addDefault3Task(taskManager);
        List<Task> tasks = getTaskManager().getTasks();
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(3, 3);
    }

    /*
       Тесты на метод public List<Epic> getEpics();
    */
    @Test
    public void testSuccessGetEpicsEmptyList() {
        List<Epic> epics = getTaskManager().getEpics();
        Assertions.assertNotNull(epics);
        Assertions.assertTrue(epics.isEmpty());
    }

    @Test
    public void testSuccessGetEpicsListWith3Epics() {
        TestDataUtil.addDefault3Epics(taskManager);
        List<Epic> epics = getTaskManager().getEpics();
        Assertions.assertNotNull(epics);
        Assertions.assertEquals(3, 3);
    }

    /*
       Тесты на метод public List<SubTask> getSubTasks();
    */
    @Test
    public void testSuccessGetSubtasksEmptyList() {
        List<Epic> epics = getTaskManager().getEpics();
        Assertions.assertNotNull(epics);
        Assertions.assertTrue(epics.isEmpty());
    }

    @Test
    public void testSuccessGetSubTasksListWith3Subtasks() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        List<SubTask> subTasks = getTaskManager().getSubTasks();
        Assertions.assertNotNull(subTasks);
        Assertions.assertEquals(3, 3);
    }

    /*
         Тесты на метод public Task getTask(int id);
      */
    @Test
    public void testGetTaskGetNullWhenNoExistIdWhenTaskListIsEmpty() {
        Task task = getTaskManager().getTask(100);
        Assertions.assertNull(task);
    }

    @Test
    public void testGetTaskGetNullWhenNoExistIdWhenTaskListIsNoEmpty() {
        TestDataUtil.addDefault3Task(taskManager);
        Task task = getTaskManager().getTask(100);
        Assertions.assertNull(task);
    }

    @Test
    public void testSuccessGetTask() {
        Task taskExpected = TestDataUtil.createDefaultTask();
        int id = getTaskManager().addTask(taskExpected);
        Task taskActual = getTaskManager().getTask(id);
        Assertions.assertEquals(taskExpected, taskActual);
    }

    /*
         Тесты на метод public Epic getEpic(int id);
      */
    @Test
    public void testGetEpicGetNullWhenNoExistIdWhenTaskListIsEmpty() {
        Epic epic = getTaskManager().getEpic(12);
        Assertions.assertNull(epic);
    }

    @Test
    public void testGetEpicGetNullWhenNoExistIdWhenTaskListIsNoEmpty() {
        TestDataUtil.addDefault3Epics(taskManager);
        Epic epic = getTaskManager().getEpic(12);
        Assertions.assertNull(epic);
    }

    @Test
    public void testSuccessGetEpic() {
        Epic epicExpected = TestDataUtil.createDefaultEpicWithOutSubtasks();
        int id = getTaskManager().addEpic(epicExpected);
        Task epicActual = getTaskManager().getEpic(id);
        Assertions.assertEquals(epicExpected, epicActual);
    }

    /*
         Тесты на метод public SubTask getSubTask(int id);
      */
    @Test
    public void testGetSubTaskGetNullWhenNoExistIdWhenTaskListIsEmpty() {
        SubTask subTask = getTaskManager().getSubTask(12);
        Assertions.assertNull(subTask);
    }

    @Test
    public void testGetSubTaskGetNullWhenNoExistIdWhenTaskListIsNoEmpty() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        SubTask subTask = getTaskManager().getSubTask(12);
        Assertions.assertNull(subTask);
    }

    @Test
    public void testSuccessGetSubTask() {
        SubTask subTaskExpected = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        int id = getTaskManager().addSubTask(subTaskExpected);
        Task epicActual = getTaskManager().getSubTask(id);
        Assertions.assertEquals(subTaskExpected, epicActual);
    }

    /*
         Тесты на метод public void updateTask(Task task);
     */
    @Test
    public void testSuccessUpdateTask() {
        Task task = TestDataUtil.createDefaultTask();
        int id = getTaskManager().addTask(task);
        task.setId(id);
        task.setName("Update name");
        task.setDescription("Update description");
        getTaskManager().updateTask(task);
        Assertions.assertEquals(task, getTaskManager().getTask(task.getId()));
    }

    @Test
    public void testNoUpdateTaskWhenNullTask() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateTask(null));

        Assertions.assertEquals("Передано null-значение.", ex.getMessage());
        Assertions.assertTrue(getTaskManager().getTasks().isEmpty());
    }

    @Test
    public void testNoUpdateTaskWhenNoExistIdTask() {
        Task task = TestDataUtil.createDefaultTask();
        getTaskManager().addTask(task);
        task.setId(101);


        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateTask(task));

        Assertions.assertEquals("Обновление невозможно, такая задача не найдена.", ex.getMessage());
    }

    @Test
    public void testNoUpdateTaskIntersectTask() {
        Task task = TestDataUtil.createDefaultTask();
        task.setDuration(60);
        task.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addTask(task);

        Task task3 = TestDataUtil.createDefaultTask();
        task3.setDuration(120);
        task3.setStartTime(LocalDateTime.parse("2023-01-20T13:30"));
        getTaskManager().addTask(task3);

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setId(id);
        task2.setDuration(120);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T13:00"));

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateTask(task2));

        Assertions.assertEquals("Обновление невозможно," +
                " так как данная задача пересекается с другой запланированной задачей.", ex.getMessage());
    }

    @Test
    public void testUpdateTaskWithNotNullStartTime() {
        Task task = TestDataUtil.createDefaultTask();
        task.setDuration(60);
        task.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addTask(task);

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setId(id);
        task2.setDuration(120);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T13:01"));

        getTaskManager().updateTask(task2);

        Task taskActual = getTaskManager().getTask(id);
        Assertions.assertEquals(task2, taskActual);
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T13:01"),
                taskActual.getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T15:01"),
                taskActual.getEndTime());
    }

    @Test
    public void testUpdateTaskWithNullStartTime() {
        Task task = TestDataUtil.createDefaultTask();
        task.setDuration(60);
        task.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addTask(task);

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setId(id);
        task2.setDuration(0);
        task2.setStartTime(null);

        getTaskManager().updateTask(task2);

        Task taskActual = getTaskManager().getTask(id);
        Assertions.assertEquals(task2, taskActual);
        Assertions.assertNull(task2.getStartTime());
        Assertions.assertNull(task2.getEndTime());
    }

    /*
             Тесты на метод  public void updateEpic(Epic epic);
         */
    @Test
    public void testSuccessUpdateEpicWithOutSubTask() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        int id = getTaskManager().addEpic(epic);
        epic.setId(id);
        epic.setName("Update name");
        epic.setDescription("Update description");
        getTaskManager().updateEpic(epic);
        Assertions.assertEquals(epic, getTaskManager().getEpic(epic.getId()));
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testSuccessUpdateEpicWith3SubTask() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        int id = getTaskManager().addEpic(epic);
        TestDataUtil.add3InProgressSubtasksByEpic(epic, getTaskManager());

        epic.setId(id);
        epic.setName("Update name");
        epic.setDescription("Update description");
        getTaskManager().updateEpic(epic);

        Assertions.assertEquals(epic, getTaskManager().getEpic(epic.getId()));
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }


    @Test
    public void testNoUpdateEpicWhenNullEpic() {
        Assertions.assertTrue(getTaskManager().getEpics().isEmpty());

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateEpic(null));

        Assertions.assertEquals("Передано null-значение.", ex.getMessage());
        Assertions.assertTrue(getTaskManager().getEpics().isEmpty());
    }

    @Test
    public void testNoUpdateEpicWhenNoExistIdEpic() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        getTaskManager().addEpic(epic);
        epic.setId(101);

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateEpic(epic));

        Assertions.assertEquals("Обновление невозможно, такой эпик не найден.", ex.getMessage());

        Assertions.assertNull(getTaskManager().getEpic(101));
    }

    /*
             Тесты на метод public void updateSubTask(SubTask subTask);
         */
    @Test
    public void testSuccessUpdateSubTask() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        int id = getTaskManager().addSubTask(subTask);
        subTask.setId(id);
        subTask.setName("Update name");
        subTask.setDescription("Update description");
        subTask.setStatus(Status.DONE);
        getTaskManager().updateSubTask(subTask);
        Assertions.assertEquals(subTask, getTaskManager().getSubTask(subTask.getId()));
        Assertions.assertEquals(Status.DONE, getTaskManager().getEpics().get(0).getStatus());
    }

    @Test
    public void testNoUpdateSubTaskWhenNullTask() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateSubTask(null));

        Assertions.assertEquals("Передано null-значение.", ex.getMessage());
        Assertions.assertTrue(getTaskManager().getSubTasks().isEmpty());
    }

    @Test
    public void testNoUpdateSubTaskWhenNoExistIdSubTask() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(taskManager);
        getTaskManager().addSubTask(subTask);
        subTask.setId(101);

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateSubTask(subTask));

        Assertions.assertEquals("Нет такой сабтаски, подзадача не добавилась.", ex.getMessage());

        Assertions.assertNull(getTaskManager().getSubTask(101));
    }

    // Тест перепривязки подзадачи к другому эпику
    @Test
    public void testSuccessUpdateSubTaskChangeEpic() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        int id = getTaskManager().addSubTask(subTask);
        Epic epic2 = TestDataUtil.createDefaultEpicWithOutSubtasks();
        getTaskManager().addEpic(epic2);
        subTask.setEpicId(epic2.getId());
        subTask.setStatus(Status.IN_PROGRESS);
        getTaskManager().updateSubTask(subTask);
        Assertions.assertEquals(epic2,
                getTaskManager().getEpic(getTaskManager().getSubTask(id).getEpicId()));
        Assertions.assertEquals(Status.IN_PROGRESS, epic2.getStatus());
    }

    @Test
    public void testNoUpdateSubTaskNoExistEpic() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        int id = getTaskManager().addSubTask(subTask);
        int epicId = getTaskManager().getSubTask(id).getEpicId();
        SubTask subTask2 = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask2.setId(id);
        subTask2.setEpicId(102);

        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateSubTask(subTask2));

        Assertions.assertEquals("Нет эпика подзадачи, подзадача не добавилась.", ex.getMessage());

        Assertions.assertEquals(epicId, getTaskManager().getSubTask(id).getEpicId());

    }

    @Test
    public void testUpdateSubTaskWithNotNullStartTime() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addSubTask(subTask);

        SubTask task2 = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        task2.setId(id);
        task2.setDuration(120);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T13:01"));

        getTaskManager().updateSubTask(task2);

        SubTask taskActual = getTaskManager().getSubTask(id);
        Assertions.assertEquals(task2, taskActual);
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T13:01"),
                taskActual.getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T15:01"),
                taskActual.getEndTime());
    }

    @Test
    public void testUpdateSubTaskWithNullStartTime() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addSubTask(subTask);

        SubTask task2 = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        task2.setId(id);
        task2.setDuration(120);
        task2.setStartTime(null);

        getTaskManager().updateSubTask(task2);

        SubTask taskActual = getTaskManager().getSubTask(id);
        Assertions.assertEquals(task2, taskActual);
        Assertions.assertNull(task2.getStartTime());
        Assertions.assertNull(task2.getEndTime());
    }

    @Test
    public void testNoUpdateSubTaskIntersectSubTask() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        int id = getTaskManager().addSubTask(subTask);

        SubTask task2 = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        task2.setDuration(120);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T13:02"));
        getTaskManager().addSubTask(task2);

        SubTask task3 = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        task3.setId(id);
        task3.setDuration(120);
        task3.setStartTime(LocalDateTime.parse("2023-01-20T12:30"));


        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().updateSubTask(task3));

        Assertions.assertEquals("Обновление невозможно," +
                " так как данная задача пересекается с другой запланированной задачей.", ex.getMessage());
    }

        /*
             Тесты на метод  public void removeTaskById(int id);
         */

    @Test
    public void testSuccessRemoveTaskById() {
        Task task = TestDataUtil.createDefaultTask();
        int id = getTaskManager().addTask(task);
        getTaskManager().removeTaskById(id);
        Assertions.assertNull(taskManager.getTask(id));
    }

    @Test
    public void testRemoveTaskByIdGetNullWhenRemoveNoExistId() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().removeTaskById(102));

        Assertions.assertEquals("Нет задачи c id:102.", ex.getMessage());

        Assertions.assertNull(taskManager.getTask(102));
    }
        /*
             Тесты на метод  public void removeEpicById(int id);
         */

    @Test
    public void testSuccessRemoveEpicByIdWithOutSubtasks() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        int id = getTaskManager().addEpic(epic);
        getTaskManager().removeEpicById(id);
        Assertions.assertNull(taskManager.getEpic(id));
    }

    @Test
    public void testSuccessRemoveEpicByIdWith3Subtasks() {
        Epic epic = TestDataUtil.createDefaultEpicWithOutSubtasks();
        int id = getTaskManager().addEpic(epic);
        TestDataUtil.add3InProgressSubtasksByEpic(epic, getTaskManager());
        getTaskManager().removeEpicById(id);
        Assertions.assertNull(taskManager.getEpic(id));
        Assertions.assertTrue(taskManager.getEpics().isEmpty());
        Assertions.assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    public void testRemoveEpicByIdGetNullWhenRemoveNoExistId() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().removeEpicById(100));

        Assertions.assertEquals("Нет эпика c id:100.", ex.getMessage());

        Assertions.assertNull(taskManager.getEpic(100));
    }

    /*
             Тесты на метод  public void removeSubTaskById(int id);
    */

    @Test
    public void testSuccessRemoveSubTaskByIdOneTask() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask.setStatus(Status.IN_PROGRESS);
        int id = getTaskManager().addSubTask(subTask);
        int epicId = getTaskManager().getSubTask(id).getEpicId();
        getTaskManager().removeSubTaskById(id);
        Assertions.assertNull(taskManager.getSubTask(id));
        Assertions.assertEquals(Status.NEW, getTaskManager().getEpic(epicId).getStatus());
    }

    @Test
    public void testSuccessRemoveSubTaskByIdOneTaskNotNullStartTime() {
        SubTask subTask = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        subTask.setDuration(60);
        subTask.setStartTime(LocalDateTime.parse("2023-01-20T14:01"));
        int id = getTaskManager().addSubTask(subTask);
        int epicId = getTaskManager().getSubTask(id).getEpicId();
        getTaskManager().removeSubTaskById(id);
        Assertions.assertNull(taskManager.getSubTask(id));
        Assertions.assertNull(taskManager.getEpic(epicId).getStartTime());
        Assertions.assertNull(taskManager.getEpic(epicId).getEndTime());
    }

    @Test
    public void testSuccessRemoveSubTaskById3TaskNotNullStartTimeRemoveFirst() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        getTaskManager().getSubTask(subTaskIds.get(0)).setStartTime(LocalDateTime.parse("2023-01-20T12:30"));
        getTaskManager().getSubTask(subTaskIds.get(0)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(1)).setStartTime(LocalDateTime.parse("2023-01-20T13:31"));
        getTaskManager().getSubTask(subTaskIds.get(1)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(2)).setStartTime(LocalDateTime.parse("2023-01-20T14:32"));
        getTaskManager().getSubTask(subTaskIds.get(2)).setDuration(60);
        getTaskManager().updateEpic(epic);

        int id = subTaskIds.get(0);
        int epicId = getTaskManager().getSubTask(id).getEpicId();

        getTaskManager().removeSubTaskById(id);

        Assertions.assertNull(taskManager.getSubTask(id));
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T13:31"), taskManager.getEpic(epicId).getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T15:32"), taskManager.getEpic(epicId).getEndTime());
    }

    @Test
    public void testSuccessRemoveSubTaskById3TaskNotNullStartTimeRemoveMiddle() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        getTaskManager().getSubTask(subTaskIds.get(0)).setStartTime(LocalDateTime.parse("2023-01-20T12:30"));
        getTaskManager().getSubTask(subTaskIds.get(0)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(1)).setStartTime(LocalDateTime.parse("2023-01-20T13:31"));
        getTaskManager().getSubTask(subTaskIds.get(1)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(2)).setStartTime(LocalDateTime.parse("2023-01-20T14:32"));
        getTaskManager().getSubTask(subTaskIds.get(2)).setDuration(60);
        getTaskManager().updateEpic(epic);

        int id = subTaskIds.get(1);
        int epicId = getTaskManager().getSubTask(id).getEpicId();

        getTaskManager().removeSubTaskById(id);

        Assertions.assertNull(taskManager.getSubTask(id));
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:30"), taskManager.getEpic(epicId).getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T15:32"), taskManager.getEpic(epicId).getEndTime());
    }

    @Test
    public void testSuccessRemoveSubTaskById3TaskNotNullStartTimeRemoveEnd() {
        TestDataUtil.addDefault1EpicWith3SubTask(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        getTaskManager().getSubTask(subTaskIds.get(0)).setStartTime(LocalDateTime.parse("2023-01-20T12:30"));
        getTaskManager().getSubTask(subTaskIds.get(0)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(1)).setStartTime(LocalDateTime.parse("2023-01-20T13:31"));
        getTaskManager().getSubTask(subTaskIds.get(1)).setDuration(60);
        getTaskManager().getSubTask(subTaskIds.get(2)).setStartTime(LocalDateTime.parse("2023-01-20T14:32"));
        getTaskManager().getSubTask(subTaskIds.get(2)).setDuration(60);
        getTaskManager().updateEpic(epic);

        int id = subTaskIds.get(2);
        int epicId = getTaskManager().getSubTask(id).getEpicId();

        getTaskManager().removeSubTaskById(id);

        Assertions.assertNull(taskManager.getSubTask(id));
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:30"), taskManager.getEpic(epicId).getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T14:31"), taskManager.getEpic(epicId).getEndTime());
    }

    @Test
    public void testRemoveSubTaskByIdGetNullWhenRemoveNoExistId() {
        TaskValidationException ex = Assertions.assertThrows(TaskValidationException.class, () ->
                getTaskManager().removeSubTaskById(100));

        Assertions.assertEquals("Нет подзадачи c id:100.", ex.getMessage());
    }

    /*
         Тесты на метод  List<SubTask> getAllSubTaskByEpicId(int epicId)
*/
    @Test
    public void testGetAllSubTaskByEpicId() {
        TestDataUtil.addDefault1EpicWith3SubTask(getTaskManager());
        List<SubTask> subTasks = getTaskManager().getAllSubTaskByEpicId(getTaskManager().getEpics().get(0).getId());
        Assertions.assertEquals(3, subTasks.size());
    }

    @Test
    public void testGetAllSubTaskByEpicIdNoExistEpicId() {
        List<SubTask> subTasks = getTaskManager().getAllSubTaskByEpicId(200);
        Assertions.assertTrue(subTasks.isEmpty());
    }

    /*
         Тесты на метод public List<Task> getHistory();
*/
    @Test
    public void testGetHistoryIsEmptyHistory() {
        Assertions.assertNotNull(getTaskManager().getHistory());
    }

    @Test
    public void testGetHistoryNoEmptyHistory() {
        TestDataUtil.addDefault3Task(getTaskManager());
        TestDataUtil.addDefault3Epics(getTaskManager());
        for (Task task : getTaskManager().getTasks()) {
            getTaskManager().getTask(task.getId());
        }
        for (Epic epic : getTaskManager().getEpics()) {
            getTaskManager().getTask(epic.getId());
        }
        Assertions.assertNotNull(getTaskManager().getHistory());
    }

        /*
         Тесты на метод public List<Task> getPrioritizedTasks()
*/

    @Test
    public void testGetPrioritizedTasksEmptyList() {
        Assertions.assertNotNull(getTaskManager().getPrioritizedTasks());
        Assertions.assertTrue(getTaskManager().getPrioritizedTasks().isEmpty());
    }

    @Test
    public void testGetPrioritizedTasksOrder() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setDuration(58);
        task1.setStartTime(LocalDateTime.parse("2023-01-20T14:01"));

        SubTask task2 = TestDataUtil.createDefaultSubTaskWithEpic(getTaskManager());
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));

        getTaskManager().addTask(task1);
        getTaskManager().addTask(task2);

        List<Task> list = getTaskManager().getPrioritizedTasks();

        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T12:01"),
                list.get(0).getStartTime());
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T14:01"),
                list.get(1).getStartTime());
    }

    @Test
    public void testGetPrioritizedTasksOrderWithNullStartTime() {
        Task task1 = TestDataUtil.createDefaultTask();

        Task task2 = TestDataUtil.createDefaultTask();
        task2.setDuration(60);
        task2.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));

        Task task3 = TestDataUtil.createDefaultTask();
        task3.setDuration(58);
        task3.setStartTime(LocalDateTime.parse("2023-01-20T14:01"));

        getTaskManager().addTask(task1);
        getTaskManager().addTask(task2);
        getTaskManager().addTask(task3);

        List<Task> list = getTaskManager().getPrioritizedTasks();

        Assertions.assertNull(list.get(2).getStartTime());

    }

}
