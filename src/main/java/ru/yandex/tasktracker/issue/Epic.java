package ru.yandex.tasktracker.issue;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task {
    private Set<Integer> subtaskIds = new HashSet<>();

    public Epic() {
        super("Noname", "No description", Status.NEW);
    }

    public Epic(String name) {
        super(name, "Not set", Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Set<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    boolean hasSubtaskId(Integer subtaskId) {
        return subtaskIds != null && subtaskIds.contains(subtaskId);
    }

    public void addSubtaskId(Integer subTaskId) {
        subtaskIds.add(subTaskId);
    }

    public void removeSubtaskId(Integer subTaskId) {
        if (hasSubtaskId(subTaskId)) {
            subtaskIds.remove(subTaskId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds)
                && Objects.equals(name, epic.name)
                && Objects.equals(description, epic.description)
                && Objects.equals(status, epic.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, name, description, status);
    }
}