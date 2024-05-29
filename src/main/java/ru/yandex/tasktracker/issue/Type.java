package ru.yandex.tasktracker.issue;

public enum Type {
    EPIC("Epic"),
    TASK("Task"),
    SUBTASK("Subtask");

    private final String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}