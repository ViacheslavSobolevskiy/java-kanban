package ru.yandex.kanban.service;

import ru.yandex.kanban.exception.ManagerSaveException;
import ru.yandex.kanban.issue.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements AutoCloseable {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }

        if (!file.exists()) {
            throw new ManagerSaveException("Файл " + file.getAbsolutePath() + "не существует.");
        }

        if (!file.canRead()) {
            throw new ManagerSaveException("Файл " + file.getAbsolutePath() + "недоступен для чтения.");
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            // Пропускаем заголовок
            reader.readLine();

            List<Subtask> subtasksToLink = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Пропускаем пустую строку
                if (line.isEmpty()) {
                    continue;
                }

                Task task = manager.fromString(line);
                switch (task.getType()) {
                    case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(task.getId(), subtask);
                        subtasksToLink.add(subtask);
                    }
                    case TASK -> manager.tasks.put(task.getId(), task);
                    default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getType());
                }
            }

            for (Subtask subtask : subtasksToLink) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }
            for (Epic epic : manager.epics.values()) {
                manager.refreshEpicStatusById(epic.getId());
            }
        } catch (IOException managerReadException) {
            throw new ManagerSaveException("Ошибка чтения из файлового менеджера",
                    managerReadException.getCause());
        }

        return manager;
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    protected void refreshEpicStatusById(int epicId) {
        super.refreshEpicStatusById(epicId);
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    protected void save() {
        List<String> lines = new ArrayList<>();
        lines.add("id,type,name,status,description,epic");
        try {
            for (Task task : getAllTasks()) {
                lines.add(toString(task));
            }

            for (Epic epic : getAllEpics()) {
                lines.add(toString(epic));
            }

            for (Subtask subtask : getAllSubtasks()) {
                lines.add(toString(subtask));
            }

            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException managerSaveException) {
            throw new ManagerSaveException("Ошибка сохранения в файл", managerSaveException.getCause());
        }
    }

    private String toString(Task task) {
        TaskType type = task.getType();
        String epicId = "";
        if (type == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type.name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId
        );
    }

    private Task fromString(String value) {
        String[] parts = value.split(",", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Некорректный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String epicIdStr = parts[5];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(epicIdStr);
                return new Subtask(epicId, id, name, description, status);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public void close() {
        save();
    }
}
