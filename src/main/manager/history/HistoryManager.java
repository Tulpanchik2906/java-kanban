package main.manager.history;

import main.tasks.Task;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);

    public List getHistory();

    void remove(int id);
}
