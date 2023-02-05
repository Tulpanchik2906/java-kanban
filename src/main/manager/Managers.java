package main.manager;

import main.manager.history.HistoryManager;
import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.HttpTaskManager;
import main.manager.tasks.TaskManager;

import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() throws IOException, InterruptedException {
        String url = "http://localhost:8078";
        return new HttpTaskManager(url);
    }


    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
