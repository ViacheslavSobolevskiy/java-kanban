package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.Issue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HistoryManager implements IHistoryManager {
    private static final int MAX_HISTORY_TASKS = 20;
    private final Map<Integer, Issue> historyTasks = new HashMap<>();

    public void add(Integer id, Issue issue) {
        if (issue != null) {
            if (historyTasks.size() >= MAX_HISTORY_TASKS) {
                historyTasks.remove(0);
                historyTasks.put(id, issue);
            } else {
                historyTasks.put(id, issue);
            }
        } else {
            System.out.println("Ошибка HistoryManager.add");
        }
    }

    public Set<Issue> getHistory() {
        return historyTasks.values().stream().collect(Collectors.toSet());
    }

    public void printHistory() {
        System.out.println("История задач:");
        for (Issue issue : historyTasks.values()) {
            System.out.println(issue);
        }
    }
}