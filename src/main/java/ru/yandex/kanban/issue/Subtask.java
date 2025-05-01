package ru.yandex.kanban.issue;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class Subtask extends Task{
    private Integer epicId;

    public Subtask(Integer epicId, Integer id, String name, String description, @NonNull Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer epicId, String name, String description, @NonNull Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId.equals(this.getId()))
            throw new IllegalArgumentException("Ошибка setEpicId: Subtask не может быть связан с самим собой");
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", epicId=" + this.getEpicId() +
                '}';
    }

    public Subtask copy() {
        return new Subtask(this.getEpicId(), this.getId(), this.getName(),
                this.getDescription(), this.getStatus());
    }
}
