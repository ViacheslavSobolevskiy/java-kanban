package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.*;

import java.util.List;
import java.util.Set;

public interface ITaskManager {
    Integer createTask(Task task);
    Integer createSubTask(SubTask task);
    Integer createEpic(Epic task);

    void removeTask(Integer taskId);
    void removeSubTask(Integer subTaskId);
    void removeEpic(Integer epicId);

    void deleteAllTasks();
    void deleteAllSubTasks();
    void deleteAllEpics();

    Task getTask(Integer taskId);
    SubTask getSubTask(Integer subTaskId);
    Epic getEpic(Integer epicId);

    Set<Task> getAllTasks();
    Set<SubTask> getAllSubTasks();
    Set<Epic> getAllEpics();

    void setTaskStatus(Integer taskId, Status status);
    void setSubTaskStatus(Integer subTaskId, Status status);
    void setEpicStatus(Integer epicId, Status status);

    void printTask(Integer taskId);
    void printSubTask(Integer subTaskId);
    void printEpic(Integer epicId);

    void printAllTasks();
    void printAllSubTasks();
    void printAllEpics();

    List<Task> getHistory();

    void printHistory();
}