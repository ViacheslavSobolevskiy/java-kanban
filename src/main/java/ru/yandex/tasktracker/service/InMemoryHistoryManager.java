package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.Task;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_TASKS = 10;
    private static final ArrayDeque<Task> tasks = new ArrayDeque<>(MAX_HISTORY_TASKS);


    public static void reset() {
        tasks.clear();
    }

    public void remove(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public void add(Task task) {
        Objects.requireNonNull(task);
        if (tasks.size() >= MAX_HISTORY_TASKS) {
            tasks.pollFirst();
        }

        tasks.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasks);
    }
}