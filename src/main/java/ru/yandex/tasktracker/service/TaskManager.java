package ru.yandex.tasktracker.service;

import org.jetbrains.annotations.NotNull;
import ru.yandex.tasktracker.issue.Epic;
import ru.yandex.tasktracker.issue.Status;
import ru.yandex.tasktracker.issue.Subtask;
import ru.yandex.tasktracker.issue.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    HistoryManager getHistoryManager();

    int createTask(@NotNull Task task);

    int createSubtask(@NotNull Subtask subtask);

    int createEpic(@NotNull Epic epic);

    void removeTask(int taskId);

    void removeSubtask(int subtaskId);

    void removeEpic(int epicId);

    Task getTask(int taskId);

    Subtask getSubtask(int subtaskId);

    Epic getEpic(int epicId);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Set<Task> getAllTasks();

    Set<Subtask> getAllSubtasks();

    Set<Epic> getAllEpics();

    Set<Subtask> getAllSubtasksByEpicId(int epicId);

    void updateTask(int taskId, @NotNull Task task);

    void updateSubtask(int subtaskId, @NotNull Subtask subtask);

    void updateEpic(int epicId, @NotNull Epic epic);

    void updateTaskStatus(int taskId, @NotNull Status status);

    void updateSubtaskStatus(int subtaskId, @NotNull Status status);

    List<Task> getHistory();
}
