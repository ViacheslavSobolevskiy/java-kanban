package ru.yandex.tasktracker.issue;

import lombok.Data;

import java.util.Objects;

@Data
public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Task task = (Task) other;
        return Objects.equals(description, task.description)
                && Objects.equals(name, task.name)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }
}