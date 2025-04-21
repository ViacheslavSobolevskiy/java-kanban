package ru.yandex.kanban.issue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Subtask extends Task {
    private Long epicId;

    public Subtask(@NonNull Long epicId, @NonNull Long id, String name, String description, @NonNull Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(@NonNull Long epicId, String name, String description, @NonNull Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }
}
