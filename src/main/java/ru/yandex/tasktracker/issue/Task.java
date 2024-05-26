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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Task task = (Task) o;
        return Objects.equals(description, task.description)
                && Objects.equals(name, task.name)
                && status == task.status;
    }

    @Override
    public String toString() {
        return  getId() + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ",\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getStatus(),
                getDescription());
    }
}
