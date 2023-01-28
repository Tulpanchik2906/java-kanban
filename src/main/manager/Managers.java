package main.manager;

import main.manager.history.HistoryManager;
import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.FileBackedTasksManager;
import main.manager.tasks.InMemoryTaskManager;
import main.manager.tasks.TaskManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() throws IOException {
        Path pathTaskManager = Paths.get("managerState.txt");

        return new FileBackedTasksManager(pathTaskManager);
    }


    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
