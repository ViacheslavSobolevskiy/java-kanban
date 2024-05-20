package ru.yandex.tasktracker.issue;

import lombok.Data;

import java.util.Objects;

@Data
public abstract class Issue {
    protected String name;
    protected String description;
    protected Status status;

    @Override
    public String toString() {
        return getName() + ","
                + getStatus() + ","
                + getDescription() + ",\n";
    }
}
