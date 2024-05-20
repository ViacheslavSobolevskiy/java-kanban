package ru.yandex.tasktracker.issues;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Issue {
    private Integer id;
    private String name;
    private Status status;
    private String description;
    private Type type;
}
