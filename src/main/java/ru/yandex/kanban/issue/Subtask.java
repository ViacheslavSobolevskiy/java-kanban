package ru.yandex.kanban.issue;

public class Subtask extends Task implements Cloneable {
    private final Integer epicId;

    public Subtask(Integer epicId, Integer id, String name, String description, Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer epicId, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", epicId=" + this.getEpicId() +
                '}';
    }

    @Override
    public Subtask clone() {
        return (Subtask) super.clone();
    }
}
