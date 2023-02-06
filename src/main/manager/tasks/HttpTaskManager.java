package main.manager.tasks;

import com.google.gson.Gson;
import main.manager.tasks.exception.ManagerSaveException;
import main.servers.kvserver.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super();
        gson = new Gson();
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        try {
            String json = gson.toJson(super.getStringForSave());
            kvTaskClient.put("data", json);
        } catch (InterruptedException | IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }


    private void loadFromServer() throws IOException, InterruptedException {
        String json = kvTaskClient.load("data");
        String res = gson.fromJson(json, String.class);

        super.load(res);
    }


    public static HttpTaskManager loadHttpTaskManager(String url) throws IOException, InterruptedException {
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        httpTaskManager.loadFromServer();
        return httpTaskManager;
    }
}
