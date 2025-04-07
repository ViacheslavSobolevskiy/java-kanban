package ru.yandex.kanban.issue;

import lombok.Getter;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Epic extends Task {
    private final Set<Long> dependentSubtaskIds = new HashSet<>();

    public Epic(Long id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void updateSubtaskId(Long subtaskId) {
        dependentSubtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(Long subtaskId) {
        if (dependentSubtaskIds.contains(subtaskId))
            dependentSubtaskIds.remove(subtaskId);
        else
            throw new IllegalArgumentException("Subtask not found " + subtaskId);
    }
}
