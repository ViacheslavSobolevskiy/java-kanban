package ru.yandex.tasktracker.issues;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;

    /**
     * Для соответствия тех заданию.
     * Lombok и так генерит правильные методы hashCode, equals, toString.
     * Код взят из примера sprint 4.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                        && Objects.equals(description, task.description)
                        && id == task.id
                        && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, name, status);
    }

    @Override
    public String toString() {
        return id + "," + name + "," + status + "," + description + ",\n";
    }
}
