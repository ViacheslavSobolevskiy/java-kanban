package ru.yandex.kanban.issue;

import lombok.Getter;

@Getter
public class Subtask extends Task {
    private final Long epicId;

    public Subtask(Long epicId, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Long id, Long epicId, String name, String description, Status status) {
        super(name, description, status);
        this.id = id;
        this.epicId = epicId;
    }
}
