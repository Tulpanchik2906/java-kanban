package manager.tasks;


import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    public int addTask(Task task);

    public int addEpic(Epic epic);

    public int addSubTask(SubTask task);

    public void cleanTasks();

    public void cleanEpics();

    public void cleanSubTasks();

    public List<Task> getTasks();

    public List<Epic> getEpics();

    public List<SubTask> getSubTasks();

    public Task getTask(int id);

    public Epic getEpic(int id);

    public SubTask getSubTask(int id);

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubTask(SubTask subTask);

    public void removeTaskById(String id);

    public void removeEpicById(int id);

    public void removeSubTaskById(int id);

    public List<SubTask> getAllSubTaskByEpicId(int epicId);

    public List<Task> getHistory();
}
