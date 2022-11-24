package manager.tasks;

import manager.Managers;
import manager.history.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, SubTask> subTasks;
    private final Map<Integer, Epic> epics;

    private final HistoryManager historyManager;

    private static int id = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }


    public static int generateNewId() {
        id++;
        return id;
    }

    @Override
    public int addTask(Task task) {
        // присваиваем id задачи
        task.setId(generateNewId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(generateNewId());
        epics.put(epic.getId(), epic);
        // Пересчитываем статус эпика
        epic.setStatus(getEpicStatus(epic));
        return epic.getId();
    }

    @Override
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

    @Override
    public void cleanTasks() {
        tasks.clear();
    }

    @Override
    public void cleanEpics() {
        // если удалились все эпики, то удалились и все сабтаски
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void cleanSubTasks() {
        subTasks.clear();
        // если удалились все сабтаски, то у всех эпиков статус должен быть New
        for (Epic epic : epics.values()) {
            // Удаляем все подзадачи в сушности эпика
            epic.cleanSubtaskIds();
            epic.setStatus(getEpicStatus(epic));
        }
    }

    @Override
    public ArrayList<Task> getAllTaskByTypeTask() {
        ArrayList<Task> list = new ArrayList<>();
        for (Task task : tasks.values()) {
            list.add(task);
        }
        return list;
    }

    @Override
    public ArrayList<Epic> getAllTaskByTypeEpic() {
        ArrayList<Epic> list = new ArrayList<>();
        for (Epic epic : epics.values()) {
            list.add(epic);
        }
        return list;
    }

    @Override
    public ArrayList<SubTask> getAllTaskByTypeSubTask() {
        ArrayList<SubTask> list = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            list.add(subTask);
        }
        return list;
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Обновление невозможно, такая задача не найдена");
        } else {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Обновление невозможно, такой эпик не найден");
        } else {
            epic.setStatus(getEpicStatus(epic));
            epics.put(epic.getId(), epic);
        }
    }

    @Override
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

    @Override
    public void removeTaskById(String id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Нет такой задачи");
        }
    }

    @Override
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

    @Override
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

    @Override
    public ArrayList<SubTask> getAllSubTaskByEpicId(int epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (int id : epics.get(epicId).getSubTaskIds()) {
            subTasks.add(this.subTasks.get(id));
        }
        return subTasks;
    }

    @Override
    public Status getEpicStatus(Epic epic) {
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
