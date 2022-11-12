package tasks;

import java.util.Objects;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description,  int id, Enum status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description,  Enum status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return getEpicId() == subTask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id='" + getId() + '\'' +
                ", status=" + getStatus() + ", "+
                "epicId=" + epicId +
                '}';
    }
}