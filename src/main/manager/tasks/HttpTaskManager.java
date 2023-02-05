package main.manager.tasks;

import main.manager.tasks.exception.ManagerSaveException;
import main.servers.kvserver.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super();
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        try {
            kvTaskClient.put("data", super.getStringForSave());
        } catch (InterruptedException | IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }


    private void loadFromServer() throws IOException, InterruptedException {
        String res = kvTaskClient.load("data");
        super.load(res);
    }


    public static HttpTaskManager loadHttpTaskManager(String url) throws IOException, InterruptedException {
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        httpTaskManager.loadFromServer();
        return httpTaskManager;
    }
}
