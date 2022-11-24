package manager.history;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);

    public List getHistory();
}
