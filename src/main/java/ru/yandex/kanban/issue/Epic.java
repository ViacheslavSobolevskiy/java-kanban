package ru.yandex.kanban.issue;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Эпик. Это задача, которая состоит из подзадач.
 * Каждый эпик может содержать несколько подзадач, и каждая подзадача может принадлежать только одному эпику.
 */
@Getter
public class Epic extends Task {
    private final Set<Long> dependentSubtaskIds = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(@NonNull Long id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtaskId(Long subtaskId) {
        if (dependentSubtaskIds.contains(subtaskId))
            throw new IllegalArgumentException("addSubtaskId: Subtask уже существует " + subtaskId);

        dependentSubtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(Long subtaskId) {
        if (dependentSubtaskIds.contains(subtaskId))
            dependentSubtaskIds.remove(subtaskId);
        else
            throw new IllegalArgumentException("removeSubtaskId: Subtask для удаления не найден " + subtaskId);

    }
}
