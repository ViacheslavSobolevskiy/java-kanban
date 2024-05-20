package ru.yandex.tasktracker.issue;

import lombok.Data;

import java.util.Objects;

@Data
public class SubTask extends Issue {
    private Integer epicId;

    public SubTask(String name, String description, Status status, Integer epicId) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask SubTask = (SubTask) o;
        return Objects.equals(epicId, SubTask.epicId)
                && Objects.equals(name, SubTask.name)
                && Objects.equals(description, SubTask.description)
                && Objects.equals(status, SubTask.status);
    }
}