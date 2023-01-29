package main.manager.tasks;

import main.manager.Managers;
import main.manager.history.HistoryManager;
import main.manager.tasks.exception.TaskValidationException;
import main.tasks.Epic;
import main.tasks.Status;
import main.tasks.SubTask;
import main.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, SubTask> subTasks;
    private final Map<Integer, Epic> epics;
    private final TreeSet<Task> sortedAllTasks;
    private final HistoryManager historyManager;
    private static int id = 0;

/*
    Comparator<Task> comparator2 = (o1, o2) -> {
        if (o1.getStartTime() == null) {
            return 1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }
        if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else {
            return 0;
        }
    };
*/
    Comparator<Task> comparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder()));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        sortedAllTasks = new TreeSet<>(comparator);
        historyManager = Managers.getDefaultHistory();
    }


    public static int generateNewId() {
        id++;
        return id;
    }

    @Override
    public int addTask(Task task) {
        if(task == null){
            throw new TaskValidationException("Передано null-значение.");
        }
        // Проверяем можено ли добавить задачу без пересечения с другими задачами
        if (checkIntersection(task)) {
            throw new TaskValidationException("Задача имеет пересечение с другими задачами. Добавление не произошло.");
        }
        // Проверка есть ли такой номер в менеджере задач
        if (containsIdInTaskManager(task)) {
            throw new TaskValidationException(
                    "Задача с id=" + task.getId() + " уже существует, добавление не произошло.");
        }
        // Проверка есть ли у задачи номер (если нет, то присваиваем)
        if (task.getId() == 0) {
            // присваиваем id задачи
            task.setId(generateNewId());
        }
        tasks.put(task.getId(), task);
        sortedAllTasks.add(task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if(epic == null){
            throw new TaskValidationException("Передано null-значение.");
        }
        // Проверка есть ли такой номер в менеджере задач
        if (containsIdInTaskManager(epic)) {
            throw new TaskValidationException("Эпик с id=" + epic.getId() + " уже существует, добавление не произошло.");
        }
        // Проверяем есть ли у задачи номер (если нет, то присваиваем)
        if (epic.getId() == 0) {
            // присваиваем id задачи
            epic.setId(generateNewId());
        }
        epics.put(epic.getId(), epic);
        // Пересчитываем статус эпика
        epic.setStatus(getEpicStatus(epic));
        epic.setStartTime(getStartTimeEpic(epic));
        epic.setEndTime(getEndTimeEpic(epic));

        return epic.getId();
    }

    @Override
    public int addSubTask(SubTask subTask) {
        if(subTask == null){
            throw new TaskValidationException("Передано null-значение.");
        }
        // Проверяем, что эпик для сабтаски есть, иначе не добавляем сабтаску
        if (epics.containsKey(subTask.getEpicId())) {
            // Проверяем есть ли у задачи номер (если нет, то присваиваем)
            if (subTask.getId() == 0) {
                // присваиваем id задачи
                subTask.setId(generateNewId());
            }
            // Проверка есть ли такой номер в менеджере задач
            if (containsIdInTaskManager(subTask)) {
                throw new TaskValidationException(
                        "Подзадача с id=" + subTask.getId() + " уже существует, добавление не произошло.");
            }
            // Проверяем можено ли добавить задачу без пересечения с другими задачами
            if (checkIntersection(subTask)) {
                throw new TaskValidationException(
                        "Подзадача имеет пересечение с другими задачами. Добавление не произошло.");
            }
            subTasks.put(subTask.getId(), subTask);
            sortedAllTasks.add(subTask);
            // добавляем сабтаску в список сабтасок эпика
            final Epic epic = epics.get(subTask.getEpicId());
            epic.addSubTask(subTask);
            // пересчитываем статус эпика
            epic.setStatus(getEpicStatus(epic));
            epic.setStartTime(getStartTimeEpic(epic));
            epic.setEndTime(getEndTimeEpic(epic));
        } else {
            throw new TaskValidationException("Нет такого эпика, подзадача не добавилась.");
        }
        return subTask.getId();
    }

    private boolean containsIdInTaskManager(Task task) {
        return tasks.containsKey(task.getId()) ||
                epics.containsKey(task.getId()) || subTasks.containsKey(task.getId());
    }

    @Override
    public void cleanTasks() {
        //Удаление из истории просмотров
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        for (Task task : tasks.values()) {
            sortedAllTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void cleanEpics() {
        //Удаление из истории просмотров
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (SubTask subTask : subTasks.values()) {
            sortedAllTasks.remove(subTask);
        }
        // если удалились все эпики, то удалились и все сабтаски
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void cleanSubTasks() {
        //Удаление из истории просмотров
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (SubTask subTask : subTasks.values()) {
            sortedAllTasks.remove(subTask);
        }
        subTasks.clear();
        // если удалились все сабтаски, то у всех эпиков статус должен быть New
        for (Epic epic : epics.values()) {
            // Удаляем все подзадачи в сушности эпика
            epic.cleanSubtaskIds();
            epic.setStatus(getEpicStatus(epic));
            epic.setStartTime(getStartTimeEpic(epic));
            epic.setEndTime(getEndTimeEpic(epic));
        }
    }

    @Override
    public List<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        list.addAll(tasks.values());
        return list;
    }

    @Override
    public List<Epic> getEpics() {
        ArrayList<Epic> list = new ArrayList<>();
        list.addAll(epics.values());
        return list;
    }

    @Override
    public List<SubTask> getSubTasks() {
        ArrayList<SubTask> list = new ArrayList<>();
        list.addAll(subTasks.values());
        return list;
    }

    @Override
    public Task getTask(int id) {
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
        if (task == null) {
            throw new TaskValidationException("Передано null-значение.");
        }
        if (!tasks.containsKey(task.getId())) {
            throw new TaskValidationException("Обновление невозможно, такая задача не найдена.");
        } else {
            // Проверяем можено ли добавить задачу без пересечения с другими задачами
            if (!checkIntersection(task)) {
                tasks.put(task.getId(), task);
                sortedAllTasks.remove(task);
                sortedAllTasks.add(task);
            } else {
                throw new TaskValidationException("Обновление невозможно," +
                        " так как данная задача пересекается с другой запланированной задачей.");
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new TaskValidationException("Передано null-значение.");
        }
        if (!epics.containsKey(epic.getId())) {
            throw new TaskValidationException("Обновление невозможно, такой эпик не найден.");
        } else {
            epic.setStartTime(getStartTimeEpic(epic));
            epic.setStatus(getEpicStatus(epic));
            epic.setEndTime(getEndTimeEpic(epic));
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            throw new TaskValidationException("Передано null-значение.");
        }
        // Проверяем, что id такой сабтаски есть
        if (subTasks.containsKey(subTask.getId())) {
            // Проверяем, что эпик для сабтаски есть, иначе не добавляем сабтаску
            if (epics.containsKey(subTask.getEpicId())) {
                if (checkIntersection(subTask)) {
                    throw new TaskValidationException("Обновление невозможно," +
                            " так как данная задача пересекается с другой запланированной задачей.");
                } else {
                    subTasks.put(subTask.getId(), subTask);
                    // добавляем сабтаску в список сабтасок эпика
                    final Epic epic = epics.get(subTask.getEpicId());
                    epic.addSubTask(subTask);
                    sortedAllTasks.remove(subTask);
                    sortedAllTasks.add(subTask);
                    // пересчитываем статус эпика
                    epic.setStatus(getEpicStatus(epic));
                    epic.setStartTime(getStartTimeEpic(epic));
                    epic.setEndTime(getEndTimeEpic(epic));
                }
            } else {
                throw new TaskValidationException("Нет эпика подзадачи, подзадача не добавилась.");
            }
        } else {
            throw new TaskValidationException("Нет такой сабтаски, подзадача не добавилась.");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            sortedAllTasks.remove(getTask(id));
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new TaskValidationException("Нет задачи c id:" +id +".");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            // если удаляется epic, то удаляются и  его подзадачи
            Epic epic = epics.get(id);
            for (int subTaskId : epic.getSubTaskIds()) {
                sortedAllTasks.remove(getSubTask(subTaskId));
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            throw new TaskValidationException("Нет эпика c id:" +id +".");
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            // то удаляем задачу и пересчитываем статус для эпика
            Epic epic = epics.get(subTasks.get(id).getEpicId());
            epic.getSubTaskIds().remove((Integer) id);
            epic.setStatus(getEpicStatus(epic));
            epic.setStartTime(getStartTimeEpic(epic));
            epic.setEndTime(getEndTimeEpic(epic));
            sortedAllTasks.remove(getSubTask(id));
            subTasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new TaskValidationException("Нет подзадачи c id:" +id +".");
        }
    }

    @Override
    public List<SubTask> getAllSubTaskByEpicId(int epicId) {
        List<SubTask> subTasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (int id : epics.get(epicId).getSubTaskIds()) {
                subTasks.add(this.subTasks.get(id));
            }
        }
        return subTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedAllTasks);
    }

    private boolean checkIntersection(Task newTask) {
        List<Task> taskList = getPrioritizedTasks();
        // Если время старта задачи не установлено, то считается, что ее можно добавить
        if (newTask.getStartTime() == null) {
            return false;
        }

        for (Task task : taskList) {
            // Если задачу пытаются обновить,
            // то она уже есть в списке для сравнения и может пересечься сама с собой
            if (task.getId() != newTask.getId() && task.getStartTime() != null) {
                if (task.getStartTime().isEqual(newTask.getEndTime())) {
                    return true;
                }
                if (task.getEndTime().isEqual(newTask.getStartTime())) {
                    return true;
                }
                if (newTask.getStartTime().isBefore(task.getStartTime())
                        && newTask.getEndTime().isAfter(task.getStartTime())) {
                    return true;
                }
                if (newTask.getStartTime().isAfter(task.getStartTime())
                        && task.getEndTime().isAfter(newTask.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Расчет времени начала эпика
    private LocalDateTime getStartTimeEpic(Epic epic) {
        List<SubTask> subTaskList = getAllSubTaskByEpicId(epic.getId());
        if (subTaskList.isEmpty()) {
            return null;
        }

        Collections.sort(subTaskList, comparator);
        return subTaskList.get(0).getStartTime();
    }

    // Расчет времени окончания эпика
    private LocalDateTime getEndTimeEpic(Epic epic) {
        List<SubTask> subTaskList = getAllSubTaskByEpicId(epic.getId());
        subTaskList.sort(comparator);
        for (int i = subTaskList.size() - 1; i >= 0; i--) {
            if (subTaskList.get(i).getEndTime() != null) {
                return subTaskList.get(i).getEndTime();
            }
        }
        return null;
    }


    private Status getEpicStatus(Epic epic) {
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
