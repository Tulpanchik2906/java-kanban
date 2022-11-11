package tasks;

import manager.TaskManager;

import java.util.Objects;

public class Task {
    private String name;
    private String describe;
    private String id;
    private Enum status;

    public Task(String name, String describe) {
        this.name = name;
        this.describe = describe;
        this.id="Task - " + TaskManager.generateNewId();
    }

    public String getName() {
        return name;
    }

    public String getDescribe() {
        return describe;
    }

    public String getId() {
        return id;
    }

    public Enum getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }


    public void setStatus(Enum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(getName(), task.getName())
                && Objects.equals(getDescribe(),
                task.getDescribe())
                && Objects.equals(getId(), task.getId())
                && Objects.equals(getStatus(), task.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescribe(), getId(), getStatus());
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", id='" + id + '\'' +
                ", status=" + status +
                '}';
    }
}
