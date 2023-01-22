package test.historymanager;

import main.manager.history.HistoryManager;
import main.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.TestDataUtil;

public abstract class HistoryManagerTest<T extends HistoryManager> {

    public void setHistoryManager(T historyManager) {
        this.historyManager = historyManager;
    }

    private T historyManager;


    /*
        Тесты на метод add
     */
    @Test
    public void testSuccessAdd1Element() {
        Task task = TestDataUtil.createDefaultTask();
        historyManager.add(task);
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    public void testSuccessAdd2Element() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setId(1);
        Task task2 = TestDataUtil.createDefaultTask();
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void testAddNullTask() {
        historyManager.add(null);
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void testDuplicateAdd() {
        Task task1 = TestDataUtil.createDefaultTask();
        Task task2 = TestDataUtil.createDefaultTask();
        historyManager.add(task1);
        historyManager.add(task2);
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task1, historyManager.getHistory().get(0));
    }


    /*
        Тесты на метод testGetHistory
     */
    @Test
    public void testGetHistory() {
        Task task = TestDataUtil.createDefaultTask();
        historyManager.add(task);
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    public void testGetEmptyHistory() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());

    }


    /*
        Тесты на метод remove
    */
    // Удаление сначала списка
    @Test
    public void testRemoveBeginElement() {
        addTaskForRemoveTest();
        historyManager.remove(1);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    // Удаление из середины списка
    @Test
    public void testRemoveMiddleElement() {
        addTaskForRemoveTest();
        historyManager.remove(2);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    // Удаление из конца списка
    @Test
    public void testRemoveEndElement() {
        addTaskForRemoveTest();
        System.out.println(historyManager.getHistory());
        historyManager.remove(3);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    // Удаление из списка длины 1
    @Test
    public void testRemoveOneElement() {
        Task task1 = TestDataUtil.createDefaultTask();
        task1.setId(1);
        historyManager.add(task1);
        historyManager.remove(1);
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    // Удаление задачи с id, которого нет в списке
    @Test
    public void testRemoveNoExistElement() {
        historyManager.remove(3);
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    private void addTaskForRemoveTest() {
        Task task1 = TestDataUtil.createDefaultTask();
        Task task2 = TestDataUtil.createDefaultTask();
        Task task3 = TestDataUtil.createDefaultTask();
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }
}