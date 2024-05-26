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

    int createSubtask(@NotNull Subtask Subtask);

    int createEpic(@NotNull Epic epic);

    void removeTask(int taskId);

    void removeSubtask(int SubtaskId);

    void removeEpic(int epicId);

    Task getTask(int taskId);

    Subtask getSubtask(int SubtaskId);

    Epic getEpic(int epicId);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Set<Task> getTasks();

    Set<Subtask> getSubtasks();

    Set<Epic> getEpics();

    void setTaskStatus(int taskId, @NotNull Status status);

    void setSubtaskStatus(Integer subtaskId, Status status);

    void setEpicStatus(int epicId, @NotNull Status status);

    void printTask(int taskId);

    void printSubtask(int SubtaskId);

    void printEpic(int epicId);

    void printAllTasks();

    void printAllSubtasks();

    void printAllEpics();

    List<Task> getHistory();

    void printHistory();

    Set<Subtask> getAllSubtasksByEpicId(int epicId);

    void reset();
}
