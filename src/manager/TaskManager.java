package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;

    private static int id = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }


    public static int generateNewId() {
        id++;
        return id;
    }

    public int addTask(Task task) {
        // присваиваем id задачи
        task.setId(generateNewId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int addEpic(Epic epic) {
        epic.setId(generateNewId());
        epics.put(epic.getId(), epic);
        // Пересчитываем статус эпика
        epic.setStatus(getEpicStatus(epic));
        return epic.getId();
    }

    public int addSubTask(SubTask task) {
        // Проверяем, что эпик для сабтаски есть, иначе не добавляем сабтаску
        if (epics.containsKey(task.getEpicId())) {
            task.setId(generateNewId());
            subTasks.put(task.getId(), task);
            // добавляем сабтаску в список сабтасок эпика
            final Epic epic = epics.get(task.getEpicId());
            epic.addSubTask(task);
            // пересчитываем статус эпика
            epic.setStatus(getEpicStatus(epic));
        } else {
            System.out.println("Нет такого эпика, подзадача не добавилась");
        }
        return task.getId();
    }

    public void cleanTasks() {
        tasks.clear();
    }

    public void cleanEpics() {
        // если удалились все эпики, то удалились и все сабтаски
        epics.clear();
        subTasks.clear();
    }

    public void cleanSubTasks() {
        subTasks.clear();
        // если удалились все сабтаски, то у всех эпиков статус должен быть New
        for (Epic epic : epics.values()) {
            // Удаляем все подзадачи в сушности эпика
            epic.cleanSubtaskIds();
            epic.setStatus(getEpicStatus(epic));
        }
    }

    public ArrayList<Task> getAllTaskByTypeTask() {
        ArrayList<Task> list = new ArrayList<>();
        for (Task task : tasks.values()) {
            list.add(task);
        }
        return list;
    }

    public ArrayList<Epic> getAllTaskByTypeEpic() {
        ArrayList<Epic> list = new ArrayList<>();
        for (Epic epic : epics.values()) {
            list.add(epic);
        }
        return list;
    }

    public ArrayList<SubTask> getAllTaskByTypeSubTask() {
        ArrayList<SubTask> list = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            list.add(subTask);
        }
        return list;
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Обновление невозможно, такая задача не найдена");
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Обновление невозможно, такой эпик не найден");
        } else {
            epic.setStatus(getEpicStatus(epic));
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubTask(SubTask subTask) {
        // Проверяем, что id такой сабтаски есть
        if (subTasks.containsKey(subTask.getId())) {
            // Проверяем, что эпик для сабтаски есть, иначе не добавляем сабтаску
            if (epics.containsKey(subTask.getEpicId())) {
                subTasks.put(subTask.getId(), subTask);
                // добавляем сабтаску в список сабтасок эпика
                final Epic epic = epics.get(subTask.getEpicId());
                epic.addSubTask(subTask);
                // пересчитываем статус эпика
                epic.setStatus(getEpicStatus(epic));
            } else {
                System.out.println("Нет эпика подзадачи, подзадача не добавилась");
            }
        } else {
            System.out.println("Нет такой сабтаски, подзадача не добавилась");
        }
    }

    public void removeTaskById(String id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    public void removeEpicById(Integer id) {
        if (epics.containsKey(id)) {
            // если удаляется epic, то удаляются и  его подзадачи
            Epic epic = epics.get(id);
            for (int subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    public void removeSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {
            // то удаляем задачу и пересчитываем статус для эпика
            Epic epic = epics.get(subTasks.get(id).getEpicId());
            epic.getSubTaskIds().remove(id);
            epic.setStatus(getEpicStatus(epic));
            subTasks.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    public ArrayList<SubTask> getAllSubTaskByEpicId(int epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (int id : epics.get(epicId).getSubTaskIds()) {
            subTasks.add(this.subTasks.get(id));
        }
        return subTasks;
    }

    public Enum getEpicStatus(Epic epic) {
        List<Integer> subTaskIdsList = epic.getSubTaskIds();

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
        List<Integer> subTaskIdsList = epic.getSubTaskIds();

        for (int id : subTaskIdsList) {
            if (getSubTask(id).getStatus() != Status.NEW) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAllDoneSubTask(Epic epic) {
        List<Integer> subTaskIdsList = epic.getSubTaskIds();

        for (int id : subTaskIdsList) {
            if (getSubTask(id).getStatus() != Status.DONE) {
                return false;
            }
        }
        return true;
    }


}
