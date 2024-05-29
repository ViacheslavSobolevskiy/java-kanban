package ru.yandex.tasktracker.issue;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtaskIds = new HashSet<>();

    public Epic(String name, String s, Status aNew) {
        super(name, "Empty", Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Set<Integer> getSubtaskIds() {
        return Set.copyOf(subtaskIds);
    }

    public boolean containsSubtaskId(Integer subtaskId) {
        return subtaskIds.contains(subtaskId);
    }

    public void addSubtaskId(Integer subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic other = (Epic) o;
        return super.equals(o) &&
                Objects.equals(subtaskIds, other.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}