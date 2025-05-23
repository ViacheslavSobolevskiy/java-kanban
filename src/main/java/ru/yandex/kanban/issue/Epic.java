package ru.yandex.kanban.issue;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class Epic extends Task implements Cloneable {
    private final Set<Integer> dependentSubtaskIds = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public void addSubtaskId(@NonNull Integer subtaskId) {
        if (Objects.equals(subtaskId, this.getId()))
            throw new IllegalArgumentException("addSubtaskId: Subtask не может быть Epic'ом: " + subtaskId);

        if (dependentSubtaskIds.contains(subtaskId))
            throw new IllegalArgumentException("addSubtaskId: Subtask уже существует: " + subtaskId);

        dependentSubtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(@NonNull Integer subtaskId) {
        if (dependentSubtaskIds.contains(subtaskId))
            dependentSubtaskIds.remove(subtaskId);
        else
            throw new IllegalArgumentException("removeSubtaskId: Subtask для удаления не найден " + subtaskId);

    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }

    @Override
    public Epic clone() {
        return (Epic) super.clone();
    }
}
