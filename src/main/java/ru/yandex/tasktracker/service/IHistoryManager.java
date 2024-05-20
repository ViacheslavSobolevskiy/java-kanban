package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.Issue;

import java.util.Set;

public interface IHistoryManager {

    void add(Integer id, Issue issue);
    Set<Issue> getHistory();
    void printHistory();
}
