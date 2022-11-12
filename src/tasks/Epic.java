package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subTaskIds;


    public Epic(String name, String description, int id, Enum status) {
        super(name, description, id, status);
        subTaskIds = new ArrayList();
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTask(SubTask task) {
        // Если еще не добавляли такой id
        if (!subTaskIds.contains(task.getId())) {
            subTaskIds.add(task.getId());
        }
    }

    public void cleanSubtaskIds() {
        subTaskIds.clear();
    }

    public void removeSubtask(int id) {
        if (subTaskIds.contains(id)) {
            subTaskIds.remove((Object) id);
        }else {
            System.out.println("Нет такой сабтаски в этом эпике.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubTaskIds(), epic.getSubTaskIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubTaskIds());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id='" + getId() + '\'' +
                ", status=" + getStatus() + ", " +
                "subTaskIds=" + subTaskIds +
                '}';
    }
}
