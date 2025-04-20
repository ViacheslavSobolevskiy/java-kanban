package ru.yandex.kanban.issue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Subtask extends Task {
    private Long epicId; // Вероятно может меняться

    public Subtask(String name, String description, @NonNull Status status) {
        super(name, description, status);
    }
}
