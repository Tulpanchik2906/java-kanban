package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<String> subTaskIdsList;

    public Epic(String name, String describe) {
        super(name, describe);
        subTaskIdsList = new ArrayList();
        setStatus(getStatus());
    }

    public List<String> getSubTaskIdsList() {
        return subTaskIdsList;
    }

    public void addSubTask(SubTask task) {
        subTaskIdsList.add(task.getId());
    }

}
