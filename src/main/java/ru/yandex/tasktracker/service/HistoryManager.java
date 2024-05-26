package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task issue);

    List<Task> getHistory();

}
