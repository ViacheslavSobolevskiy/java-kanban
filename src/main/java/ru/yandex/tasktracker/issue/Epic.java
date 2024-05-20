package ru.yandex.tasktracker.issues;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Issue {
    private List<Integer> taskIdentifiers = new ArrayList<>();

    public Epic(Integer id, String name, Status status, String description) {
        super(id, name, status, description, Type.EPIC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(taskIdentifiers, epic.taskIdentifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), taskIdentifiers);
    }

    @Override
    public String toString() {
        return getId() + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ",\n";
    }
}