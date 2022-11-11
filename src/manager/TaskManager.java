package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<String, Task> tasksHashMap;
    private final HashMap<String, SubTask> subTasksHashMap;
    private final HashMap<String, Epic> epicsHashMap;

    private static int id = 0;

    public TaskManager() {
        tasksHashMap = new HashMap<>();
        subTasksHashMap = new HashMap<>();
        epicsHashMap = new HashMap<>();
    }


    public static int generateNewId() {
        id++;
        return id;
    }

    public void addTask(Task task) {
        tasksHashMap.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epicsHashMap.put(epic.getId(), epic);
        // Пересчитываем статус эпика
        epic.setStatus(getEpicStatus(epic));
    }

    public void addSubTask(SubTask task) {
        subTasksHashMap.put(task.getId(), task);
        // добавляем сабтаску в список сабтасок эпика
        task.getEpic().addSubTask(task);
        // пересчитываем статус эпика
        Epic epic = task.getEpic();
        epic.setStatus(getEpicStatus(epic));
    }

    public void cleanTasks() {
        tasksHashMap.clear();
    }

    public void cleanEpics() {
        // если удалились все эпики, то удалились и все сабтаски
        epicsHashMap.clear();
        subTasksHashMap.clear();
    }

    public void cleanSubTasks() {
        subTasksHashMap.clear();
        // если удалились все сабтаски, то у всех эпиков статус должен быть New
        for (Epic epic : epicsHashMap.values()) {
            // Удаляем все подзадачи в сушности эпика
            epic.getSubTaskIdsList().clear();
            epic.setStatus(getEpicStatus(epic));
        }
    }

    public HashMap<String, Task> getAllTaskByTypeTask() {
        return tasksHashMap;
    }

    public HashMap<String, Epic> getAllTaskByTypeEpic() {
        return epicsHashMap;
    }

    public HashMap<String, SubTask> getAllTaskByTypeSubTask() {
        return subTasksHashMap;
    }

    public Task getTask(String id) {
        return tasksHashMap.get(id);
    }

    public Epic getEpic(String id) {
        return epicsHashMap.get(id);
    }

    public SubTask getSubTask(String id) {
        return subTasksHashMap.get(id);
    }

    public void updateTask(Task task) {
        tasksHashMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epic.setStatus(getEpicStatus(epic));
        epicsHashMap.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask task) {
        subTasksHashMap.put(task.getId(), task);
        // Пересчитать статус эпика
        Epic epic = task.getEpic();
        epic.setStatus(getEpicStatus(epic));
    }

    public void removeTaskById(String id) {
        if (tasksHashMap.containsKey(id)) {
            tasksHashMap.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    public void removeEpicById(String id) {
        if (epicsHashMap.containsKey(id)) {
            // если удаляется epic, то удаляются и  его подзадачи
            Epic epic = epicsHashMap.get(id);
            for (String subTaskId : epic.getSubTaskIdsList()) {
                subTasksHashMap.remove(subTaskId);
            }
            epicsHashMap.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    public void removeSubTaskById(String id) {
        if (subTasksHashMap.containsKey(id)) {
            // то удаляем задачу и пересчитываем статус для эпика
            Epic epic = subTasksHashMap.get(id).getEpic();
            epic.getSubTaskIdsList().remove(id);
            epic.setStatus(getEpicStatus(epic));
            subTasksHashMap.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    public ArrayList<SubTask> getAllSubTaskByEpic(Epic epic) {
        ArrayList<SubTask> subTaskslist = new ArrayList<>();
        for (String id : epic.getSubTaskIdsList()) {
            subTaskslist.add(subTasksHashMap.get(id));
        }
        return subTaskslist;
    }

    public Enum getEpicStatus(Epic epic) {
        List<String> subTaskIdsList = epic.getSubTaskIdsList();

        if (subTaskIdsList.isEmpty()) {
            return Status.NEW;
        }
        if (checkAllNewSubTask(epic)) {
            return Status.NEW;
        }
        if (checkAllDoneSubTask(epic)) {
            return Status.DONE;
        }

        return Status.IN_PROGRESS;
    }

    private boolean checkAllNewSubTask(Epic epic) {
        List<String> subTaskIdsList = epic.getSubTaskIdsList();

        for (String id : subTaskIdsList) {
            if (getSubTask(id).getStatus() != Status.NEW) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAllDoneSubTask(Epic epic) {
        List<String> subTaskIdsList = epic.getSubTaskIdsList();

        for (String id : subTaskIdsList) {
            if (getSubTask(id).getStatus() != Status.DONE) {
                return false;
            }
        }
        return true;
    }


}
