package tasks;

import tasks.Epic;
import tasks.Task;

public class SubTask extends Task {

    private Epic epic;

    public SubTask(String name, String describe, Enum status, Epic epic) {
        super(name, describe);
        this.epic = epic;
        setStatus(status);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
