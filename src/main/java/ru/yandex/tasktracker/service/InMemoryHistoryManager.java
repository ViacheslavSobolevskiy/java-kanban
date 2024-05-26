package ru.yandex.tasktracker.service;

import org.jetbrains.annotations.NotNull;
import ru.yandex.tasktracker.issue.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_TASKS = 10;

    private static final List<Task> historyTasks = new LinkedList<>();

    public InMemoryHistoryManager() {
    }

    public static void reset() {
        historyTasks.clear();
    }

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