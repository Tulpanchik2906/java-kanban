package main.manager.history;

import main.tasks.Task;

import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList<>();
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return history.toList();
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

}
