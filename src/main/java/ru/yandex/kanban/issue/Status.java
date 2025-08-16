package ru.yandex.kanban.issue;

public enum Status {
    NEW("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String description;

    Status(String translation) {
        this.description = translation;
    }

    public String toString() {
        return description;
    }
}
