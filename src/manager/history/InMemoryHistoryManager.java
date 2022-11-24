package manager.history;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewTasks;

    public InMemoryHistoryManager() {
        viewTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if(viewTasks.size() == 10){
            viewTasks.remove(0);
        }
        viewTasks.add(task);
    }

    @Override
    public List getHistory() {
        return viewTasks;
    }
}
