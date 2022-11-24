package manager.tasks;


import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    public int addTask(Task task);

    public int addEpic(Epic epic);

    public int addSubTask(SubTask task);

    public void cleanTasks();

    public void cleanEpics();

    public void cleanSubTasks();

    public ArrayList<Task> getAllTaskByTypeTask();

    public ArrayList<Epic> getAllTaskByTypeEpic();

    public ArrayList<SubTask> getAllTaskByTypeSubTask();

    public Task getTask(Integer id);
    public Epic getEpic(int id);
    public SubTask getSubTask(int id);
    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubTask(SubTask subTask) ;

    public void removeTaskById(String id);

    public void removeEpicById(Integer id);

    public void removeSubTaskById(Integer id);

    public ArrayList<SubTask> getAllSubTaskByEpicId(int epicId);

    public Status getEpicStatus(Epic epic);

    public List<Task> getHistory();
}
