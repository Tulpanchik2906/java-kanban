package main.manager.tasks;

import main.servers.kvserver.KVTaskClient;

import java.io.IOException;
import java.nio.file.Path;

public class HttpTaskManager extends FileBackedTasksManager{

    private KVTaskClient kvTaskClient;
    private String url;

    private HttpTaskManager(Path path) throws IOException {
        super(path);
    }

    public HttpTaskManager(String url) throws IOException {
        super();
        this.url = url;
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save(){
        try {
            String apiToken = kvTaskClient.getApiToken();
            System.out.println("Вызыван метод save: " );
            System.out.println("Текущий токен: " +apiToken);
            kvTaskClient.save(apiToken.trim(), "1", super.getStringForSave());
        } catch (Exception ex){

        }
    }


    public void loadFromServer() throws IOException, InterruptedException {
        String apiToken = kvTaskClient.getApiToken();
        String res = kvTaskClient.load(apiToken,"1");
        super.load(res);
    }


    public static HttpTaskManager loadHttpTaskManager(String url) throws IOException, InterruptedException {
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        httpTaskManager.loadFromServer();
        return httpTaskManager;
    }
}
