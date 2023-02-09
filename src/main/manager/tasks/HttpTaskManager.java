package main.manager.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.servers.kvserver.KVTaskClient;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskType;

import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super();
        gson = new Gson();
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(super.getTasks());
        kvTaskClient.put("tasks", jsonTasks);
        String jsonEpics = gson.toJson(super.getEpics());
        kvTaskClient.put("epics", jsonEpics);
        String jsonSubTasks = gson.toJson(super.getSubTasks());
        kvTaskClient.put("subtasks", jsonSubTasks);
        String jsonHistory = gson.toJson(super.getHistory());
        kvTaskClient.put("history", jsonHistory);
    }


    private void loadFromServer() {
        String jsonTasks = kvTaskClient.load("tasks");
        String jsonEpics = kvTaskClient.load("epics");
        String jsonSubTasks = kvTaskClient.load("subtasks");
        String jsonHistory = kvTaskClient.load("history");

        List<Task> tasksList = gson.fromJson(jsonTasks, new TypeToken<List<Task>>() {
        }.getType());
        tasksList.forEach(super::addTask);

        List<Epic> epicsList = gson.fromJson(jsonEpics, new TypeToken<List<Epic>>() {
        }.getType());
        epicsList.forEach((epic) -> {
            // Если есть эпик с подзадачами, то сначала надо добавить эпик без подзадач,
            // подзадачи добавятся позже
            epic.cleanSubtaskIds();
            super.addEpic(epic);
        });

        List<SubTask> subTasksList = gson.fromJson(jsonSubTasks, new TypeToken<List<SubTask>>() {
        }.getType());
        subTasksList.forEach(super::addSubTask);

        List<Task> historyList = gson.fromJson(jsonHistory, new TypeToken<List<Task>>() {
        }.getType());
        historyList.forEach((task) -> {
            switch (TaskType.valueOf(task.getClass().getSimpleName().toUpperCase())) {
                case TASK:
                    super.getTask(task.getId());
                    break;
                case EPIC:
                    super.getEpic(task.getId());
                    break;
                case SUBTASK:
                    super.getSubTask(task.getId());
                    break;
            }
        });
    }


    public static HttpTaskManager loadHttpTaskManager(String url) {
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        httpTaskManager.loadFromServer();
        return httpTaskManager;
    }
}
