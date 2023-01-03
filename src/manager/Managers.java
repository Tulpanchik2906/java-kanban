package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.tasks.FileBackedTasksManager;
import manager.tasks.InMemoryTaskManager;
import manager.tasks.TaskManager;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTaskManager(Path path){
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
