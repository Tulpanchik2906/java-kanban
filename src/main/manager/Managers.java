package main.manager;

import main.manager.history.HistoryManager;
import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.FileBackedTasksManager;
import main.manager.tasks.HttpTaskManager;
import main.manager.tasks.InMemoryTaskManager;
import main.manager.tasks.TaskManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() throws IOException {
        String url = "http://localhost:8078";
        return new HttpTaskManager(url);
    }


    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
