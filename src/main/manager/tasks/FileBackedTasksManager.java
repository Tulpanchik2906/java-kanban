package main.manager.tasks;

import main.manager.tasks.exception.ManagerSaveException;
import main.tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    private final Map<Integer, TaskType> taskTypes;
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM");

    public FileBackedTasksManager(Path path) throws IOException {
        super();
        if (!Files.exists(path)) {
            this.path = Files.createFile(path);
        }else {
            this.path = path;
        }
        taskTypes = new HashMap<>();
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.toPath());
        fileBackedTasksManager.loadFromFile(file.toPath());
        return fileBackedTasksManager;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(path.toFile().getName(), StandardCharsets.UTF_8))) {
            bufferedWriter.append(getFirstStringForSaveInFile());
            bufferedWriter.newLine();
            saveTasks(bufferedWriter);
            saveEpics(bufferedWriter);
            saveSubTasks(bufferedWriter);
            bufferedWriter.newLine();
            bufferedWriter.append(getHistoryString());
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    private void loadFromFile(Path path) throws IOException {
        // Парсится содержимое файла
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(path.toFile().getName(), StandardCharsets.UTF_8))) {
            // Восстанавливаем задачи
            // Читается первая строка с названием колонок
            String line = bufferedReader.readLine();
            // Первая строка с задачей
            line = bufferedReader.readLine();
            // Если задач нет, то выйти из метода
            if (line == null || line.trim().isEmpty()) {
                return;
            }
            // Читаются строки пока не будет пустая строка (строка до строки с историей)
            while (!line.trim().isEmpty()) {
                String[] split = line.split(",");

                // id,type,name,status,description,epic
                int id = Integer.parseInt(split[0]);
                String name = split[2];
                Status status = Status.valueOf(split[3]);
                String description = split[4];
                int duration = 0;
                if (!split[5].trim().isEmpty()) {
                    duration = Integer.parseInt(split[5]);
                }
                LocalDateTime startTime = null;
                if (!split[6].trim().isEmpty()) {
                    startTime = LocalDateTime.parse(split[6], DATE_TIME_FORMATTER);
                }

                // В менеджер добавляются задачи в зависимости от типа задачи
                switch (TaskType.valueOf(split[1])) {
                    case TASK:
                        Task task = new Task(name, description, id, status, startTime, duration);
                        super.addTask(task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(name, description, id, status);
                        super.addEpic(epic);
                        break;
                    case SUBTASK:
                        SubTask subTask = new SubTask(name, description, id, status,
                                Integer.parseInt(split[7]), startTime, duration);
                        super.addSubTask(subTask);
                }

                taskTypes.put(id, TaskType.valueOf(split[1]));
                line = bufferedReader.readLine();
            }
            // Ввостановление истории:
            line = bufferedReader.readLine();
            recoveryHistory(line);
        }
    }

    private void recoveryHistory(String line){
        if (line == null) {
            return;
        }
        line.trim();
        String[] splitHistory = line.split(" ");
        List<String> history = Arrays.asList(splitHistory);
        Collections.reverse(history);
        for (String taskId : history) {
            switch ( taskTypes.get(Integer.parseInt(taskId))) {
                case TASK:
                    super.getTask(Integer.parseInt(taskId));
                    break;
                case EPIC:
                    super.getEpic(Integer.parseInt(taskId));
                    break;
                case SUBTASK:
                    super.getSubTask(Integer.parseInt(taskId));
                    break;
            }
        }

    }
    private String getFirstStringForSaveInFile() {
        return "id,type,name,status,description,duration,startTime,epic";
    }

    private String getTaskStringForSaveInFile(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + ","
                + getDescriptionString(task) + "," + task.getDuration()
                + "," + getStartTimeString(task) + ",";
    }

    private String getEpicStringForSaveInFile(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," + epic.getStatus() + ","
                + getDescriptionString(epic) + ", , ,";
    }

    private String getSubTaskStringForSaveInFile(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," + subTask.getName() + "," + subTask.getStatus()
                + "," + getDescriptionString(subTask) + "," + subTask.getDuration() + "," + getStartTimeString(subTask)
                + "," + subTask.getEpicId();
    }

    private String getDescriptionString(Task task) {
        String description = " ";
        if (task.getDescription() != null) {
            description = task.getDescription();
        }
        return description;
    }

    private String getStartTimeString(Task task) {
        String startTime = " ";
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(DATE_TIME_FORMATTER);
        }
        return startTime;
    }

    private void saveTasks(BufferedWriter bufferedWriter) throws IOException {
        List<Task> tasks = getTasks();
        for (Task task : tasks) {
            bufferedWriter.append(getTaskStringForSaveInFile(task));
            bufferedWriter.newLine();
        }
    }

    private void saveEpics(BufferedWriter bufferedWriter) throws IOException {
        List<Epic> epics = getEpics();
        for (Epic epic : epics) {
            bufferedWriter.append(getEpicStringForSaveInFile(epic));
            bufferedWriter.newLine();
        }
    }

    private void saveSubTasks(BufferedWriter bufferedWriter) throws IOException {
        List<SubTask> subTasks = getSubTasks();
        for (SubTask subTask : subTasks) {
            bufferedWriter.append(getSubTaskStringForSaveInFile(subTask));
            bufferedWriter.newLine();
        }
    }

    private String getHistoryString() {
        List<Task> history = getHistory();
        String res = "";
        for (int i = history.size() - 1; i >= 0; i--) {
            res += history.get(i).getId() + " ";
        }
        res.trim();
        return res;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }


    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        int id = super.addSubTask(subTask);
        save();
        return id;
    }

    @Override
    public void cleanTasks() {
        super.cleanTasks();
        save();
    }

    @Override
    public void cleanEpics() {
        super.cleanEpics();
        save();
    }

    @Override
    public void cleanSubTasks() {
        super.cleanSubTasks();
        save();
    }


    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

}
