package ru.yandex.tasktracker.issue;

import java.util.Objects;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    void setEpicId(Integer epicId) {
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