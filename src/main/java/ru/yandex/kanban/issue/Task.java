package ru.yandex.kanban.issue;

import lombok.Data;
import java.util.Objects;

@Data
public class Task {
    protected Long id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Long id,String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Task task = (Task) other;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }
}
