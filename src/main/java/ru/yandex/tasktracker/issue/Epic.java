package ru.yandex.tasktracker.issue;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Epic extends Issue {
    private final Set<Integer> subTaskIds;

    public Epic(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        subTaskIds = new HashSet<>();
    }

    boolean hasSubTaskId(Integer subtaskId) {
        return subTaskIds != null && subTaskIds.contains(subtaskId);
    }

    public void addSubTaskId(Integer subTaskId) {
        subTaskIds.add(subTaskId);
    }

    public void removeSubTaskId(Integer subTaskId) {
        if (hasSubTaskId(subTaskId)) {
            subTaskIds.remove(subTaskId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIds, epic.subTaskIds)
                && Objects.equals(name, epic.name)
                && Objects.equals(description, epic.description)
                && Objects.equals(status, epic.status);
    }
}