package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.Task;

import java.util.List;
import java.util.Set;

public interface IHistoryManager {

    void add(Task issue);
    List<Task> getHistory();
}
