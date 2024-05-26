package ru.yandex.tasktracker.issue;

public enum Type {
    EPIC("Эпик"),
    TASK("Задача"),
    SUBTASK("Подзадача");

    private final String description;

    Type(String translation) {
        this.description = translation;
    }

    @Override
    public String toString() {
        return description;
    }
}
