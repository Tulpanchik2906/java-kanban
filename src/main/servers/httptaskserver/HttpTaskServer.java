package main.servers.httptaskserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.manager.Managers;
import main.manager.tasks.TaskManager;
import main.servers.httptaskserver.enums.EndpointTasks;
import main.servers.httptaskserver.enums.RequestMethod;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final HttpServer httpServer;

    private final Gson gson;

    public HttpTaskServer() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        gson = new Gson();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
    }

    public void start() throws IOException {
        httpServer.start(); // запускаем сервер
    }

    public void stop() throws IOException {
        httpServer.stop(1); // запускаем сервер
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            EndpointTasks endpoint = route(httpExchange.getRequestURI().getPath(),
                    RequestMethod.valueOf(httpExchange.getRequestMethod()),
                    httpExchange.getRequestURI().getQuery());

            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(httpExchange);
                    break;
                }
                case GET_TASK_BY_ID: {
                    handleGetTaskById(httpExchange);
                    break;
                }
                case POST_TASK: {
                    handleAddNewTask(httpExchange);
                    break;
                }
                case PUT_TASK: {
                    handleUpdateTask(httpExchange);
                    break;
                }
                case DELETE_TASKS: {
                    handleCleanTasks(httpExchange);
                    break;
                }
                case DELETE_TASK_BY_ID: {
                    handleRemoveTask(httpExchange);
                    break;
                }
                case GET_EPICS: {
                    handleGetEpics(httpExchange);
                    break;
                }
                case GET_EPIC_BY_ID: {
                    handleGetEpicById(httpExchange);
                    break;
                }
                case POST_EPIC: {
                    handleAddNewEpic(httpExchange);
                    break;
                }
                case PUT_EPIC: {
                    handleUpdateEpic(httpExchange);
                    break;
                }
                case DELETE_EPICS: {
                    handleCleanEpics(httpExchange);
                    break;
                }
                case DELETE_EPIC_BY_ID: {
                    handleRemoveEpicById(httpExchange);
                    break;
                }
                case GET_SUBTASKS: {
                    handleGetSubTasks(httpExchange);
                    break;
                }
                case GET_SUBTASK_BY_ID: {
                    handleGetSubTaskById(httpExchange);
                    break;
                }
                case POST_SUBTASK: {
                    handleAddNewSubTask(httpExchange);
                    break;
                }
                case PUT_SUBTASK: {
                    handleUpdateSubTask(httpExchange);
                    break;
                }
                case DELETE_SUBTASKS: {
                    handleCleanSubTasks(httpExchange);
                    break;
                }
                case DELETE_SUBTASK_BY_ID: {
                    handleRemoveSubTaskById(httpExchange);
                    break;
                }
                case GET_SUBTASK_BY_EPIC_ID: {
                    handleGetEpicSubTaskById(httpExchange);
                    break;
                }
                case GET_PRIORITIZED_TASK: {
                    handleGetPrioritizedTasks(httpExchange);
                    break;
                }
                case GET_HISTORY: {
                    handleGetHistory(httpExchange);
                }
                default:
                    writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
            }
        }
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> tasks = taskManager.getTasks();
            String jsonTasks = gson.toJson(tasks);
            writeResponse(httpExchange, jsonTasks, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetTaskById(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            Task task = taskManager.getTask(Integer.parseInt(query.substring(3)));
            String jsonTasks = gson.toJson(task);
            writeResponse(httpExchange, jsonTasks, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleAddNewTask(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String taskStr = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task newTask = gson.fromJson(taskStr, Task.class);
            int id = taskManager.addTask(newTask);
            writeResponse(httpExchange, "{\"id\":"+id+"}", 201);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleUpdateTask(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String taskStr = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(taskStr, Task.class);
            taskManager.updateTask(task);
            writeResponse(httpExchange, "Задача типа Task успешно изменена.", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleCleanTasks(HttpExchange httpExchange) throws IOException {
        try {
            taskManager.cleanTasks();
            writeResponse(httpExchange, "Все задачи типа Task удалены.", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleRemoveTask(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(3));
            taskManager.removeTaskById(id);
            writeResponse(httpExchange, "Задача типа TASK c id: " + id + " успешно удалена", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        try {
            List<Epic> epics = taskManager.getEpics();
            String jsonEpics = gson.toJson(epics);
            writeResponse(httpExchange, jsonEpics, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            Epic epic = taskManager.getEpic(Integer.parseInt(query.substring(3)));
            String jsonEpic = gson.toJson(epic);
            writeResponse(httpExchange, jsonEpic, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleAddNewEpic(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String taskStr = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic newEpic = gson.fromJson(taskStr, Epic.class);
            taskManager.addEpic(newEpic);
            writeResponse(httpExchange, "Задача типа Epic успешно добавлена.", 201);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleUpdateEpic(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String taskStr = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(taskStr, Epic.class);
            taskManager.updateEpic(epic);
            writeResponse(httpExchange, "Задача типа Epic успешно изменена.", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleCleanEpics(HttpExchange httpExchange) throws IOException {
        try {
            taskManager.cleanEpics();
            writeResponse(httpExchange, "Все задачи типа Epic удалены.", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleRemoveEpicById(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(3));
            taskManager.removeEpicById(id);
            writeResponse(httpExchange, "Задача типа Epic c id: " + id + " успешно удалена", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetSubTasks(HttpExchange httpExchange) throws IOException {
        try {
            List<SubTask> subTasks = taskManager.getSubTasks();
            String jsonSubTasks = gson.toJson(subTasks);
            writeResponse(httpExchange, jsonSubTasks, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetSubTaskById(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            SubTask subTask = taskManager.getSubTask(Integer.parseInt(query.substring(3)));
            String jsonSubTask = gson.toJson(subTask);
            writeResponse(httpExchange, jsonSubTask, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleAddNewSubTask(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String taskStr = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            SubTask subTask = gson.fromJson(taskStr, SubTask.class);
            taskManager.addSubTask(subTask);
            writeResponse(httpExchange, "Задача типа SubTask успешно добавлена.", 201);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleUpdateSubTask(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String taskStr = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            SubTask subTask = gson.fromJson(taskStr, SubTask.class);
            taskManager.updateSubTask(subTask);
            writeResponse(httpExchange, "Задача типа SubTask успешно изменена.", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleCleanSubTasks(HttpExchange httpExchange) throws IOException {
        try {
            taskManager.cleanSubTasks();
            writeResponse(httpExchange, "Все задачи типа SubTasks удалены.", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleRemoveSubTaskById(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(3));
            taskManager.removeSubTaskById(id);
            writeResponse(httpExchange, "Задача типа Subtask c id: " + id + " успешно удалена", 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetEpicSubTaskById(HttpExchange httpExchange) throws IOException {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(3));
            List<SubTask> subtasks = taskManager.getAllSubTaskByEpicId(id);
            String json = gson.toJson(subtasks);
            writeResponse(httpExchange, json, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> history = taskManager.getHistory();
            String json = gson.toJson(history);
            writeResponse(httpExchange, json, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String json = gson.toJson(prioritizedTasks);
            writeResponse(httpExchange, json, 200);
        } catch (Exception ex) {
            writeResponse(httpExchange, ex.getMessage(), 500);
        }
    }


    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private EndpointTasks route(String requestPath, RequestMethod requestMethod, String query) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod == RequestMethod.GET && pathParts.length == 2 && query == null) {
            return EndpointTasks.GET_PRIORITIZED_TASK;
        }

        if (pathParts.length <= 2) {
            return EndpointTasks.UNKNOWN;
        }

        if (pathParts[2].equals("task") && requestMethod == RequestMethod.GET && pathParts.length == 3
                && query == null) {
            return EndpointTasks.GET_TASKS;
        }

        if (pathParts[2].equals("task") && requestMethod == RequestMethod.GET && pathParts.length == 3 &&
                query != null
        ) {
            return EndpointTasks.GET_TASK_BY_ID;
        }

        if (pathParts[2].equals("task") && requestMethod == RequestMethod.POST && pathParts.length == 3) {
            return EndpointTasks.POST_TASK;
        }

        if (pathParts[2].equals("task") && requestMethod == RequestMethod.PUT && pathParts.length == 3) {
            return EndpointTasks.PUT_TASK;
        }

        if (pathParts[2].equals("task") && requestMethod == RequestMethod.DELETE && pathParts.length == 3 &&
                query == null) {
            return EndpointTasks.DELETE_TASKS;
        }

        if (pathParts[2].equals("task") && requestMethod == RequestMethod.DELETE && pathParts.length == 3 &&
                query != null) {
            return EndpointTasks.DELETE_TASK_BY_ID;
        }

        if (pathParts[2].equals("epic") && requestMethod == RequestMethod.GET && pathParts.length == 3
                && query == null) {
            return EndpointTasks.GET_EPICS;
        }

        if (pathParts[2].equals("epic") && requestMethod == RequestMethod.GET && pathParts.length == 3 &&
                query != null
        ) {
            return EndpointTasks.GET_EPIC_BY_ID;
        }

        if (pathParts[2].equals("epic") && requestMethod == RequestMethod.POST && pathParts.length == 3) {
            return EndpointTasks.POST_EPIC;
        }

        if (pathParts[2].equals("epic") && requestMethod == RequestMethod.PUT && pathParts.length == 3) {
            return EndpointTasks.PUT_EPIC;
        }

        if (pathParts[2].equals("epic") && requestMethod == RequestMethod.DELETE && pathParts.length == 3 &&
                query == null) {
            return EndpointTasks.DELETE_EPICS;
        }

        if (pathParts[2].equals("epic") && requestMethod == RequestMethod.DELETE && pathParts.length == 3 &&
                query != null) {
            return EndpointTasks.DELETE_EPIC_BY_ID;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.GET && pathParts.length == 3
                && query == null) {
            return EndpointTasks.GET_SUBTASKS;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.GET && pathParts.length == 3 &&
                query != null
        ) {
            return EndpointTasks.GET_SUBTASK_BY_ID;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.POST && pathParts.length == 3) {
            return EndpointTasks.POST_SUBTASK;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.PUT && pathParts.length == 3) {
            return EndpointTasks.PUT_SUBTASK;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.DELETE && pathParts.length == 3 &&
                query == null) {
            return EndpointTasks.DELETE_SUBTASKS;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.DELETE && pathParts.length == 3 &&
                query != null) {
            return EndpointTasks.DELETE_SUBTASK_BY_ID;
        }

        if (pathParts[2].equals("subtask") && requestMethod == RequestMethod.GET && pathParts.length == 4
                && pathParts[3].equals("epic") && query != null) {
            return EndpointTasks.GET_SUBTASK_BY_EPIC_ID;
        }


        if (pathParts[2].equals("history") && requestMethod == RequestMethod.GET && pathParts.length == 3 &&
                query == null) {
            return EndpointTasks.GET_HISTORY;
        }

        return EndpointTasks.UNKNOWN;
    }
}
