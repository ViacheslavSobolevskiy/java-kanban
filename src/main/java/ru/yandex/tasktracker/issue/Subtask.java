package ru.yandex.tasktracker.issue;

import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer epicId) {
        super("Noname Subtask", "No description", Status.NEW);
        this.epicId = epicId;
    }

    public Subtask(Status status, Integer epicId) {
        super("Noname Subtask", "No description", status);
        this.epicId = epicId;
    }

    public Subtask(String description, Status status, Integer epicId) {
        super("Noname Subtask", description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask Subtask = (Subtask) o;
        return Objects.equals(epicId, Subtask.epicId)
                && Objects.equals(name, Subtask.name)
                && Objects.equals(description, Subtask.description)
                && Objects.equals(status, Subtask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId, name, description, status);
    }
}