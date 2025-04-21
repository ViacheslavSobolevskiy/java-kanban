package ru.yandex.kanban.issue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Task {
    private Long id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description, @NonNull Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(@NonNull Long id, String name, String description, @NonNull Status status) {
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
