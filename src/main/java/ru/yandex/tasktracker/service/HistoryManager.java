package ru.yandex.tasktracker.service;

import org.jetbrains.annotations.NotNull;
import ru.yandex.tasktracker.issue.Task;

import java.util.LinkedList;
import java.util.List;

public class HistoryManager implements IHistoryManager {
    private static final int MAX_HISTORY_TASKS = 5;
    private final List<Task> historyTasks = new LinkedList<>();

    @Override
    public void add(@NotNull Task task) {
        if (historyTasks.size() >= MAX_HISTORY_TASKS) {
            historyTasks.remove(0);
            historyTasks.add(task);
        } else {
            historyTasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }
}