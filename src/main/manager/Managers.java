package main.manager;

import main.manager.history.HistoryManager;
import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.FileBackedTasksManager;
import main.manager.tasks.InMemoryTaskManager;
import main.manager.tasks.TaskManager;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTaskManager(Path path) throws IOException {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
