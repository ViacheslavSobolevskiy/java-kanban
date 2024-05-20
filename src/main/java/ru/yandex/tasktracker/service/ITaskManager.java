package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.*;

import java.util.Set;

public interface ITaskManager {

    Integer createIssue(Issue issue);
    void removeIssue(Integer issueId, IssueType issueType);
    void deleteAllIssuesByType(IssueType type);
    Issue getIssueById(Integer id);
    Set<Issue> getAllByType(IssueType type);
    Set<SubTask> getAllSubTasksByEpicId(Integer id);
    void updateIssueStatus(Integer issueId, IssueType issueType);
    void printAllIssuesByType(IssueType issuetype);
    Set<Issue> getHistory();
    void setIssueStatus(Integer issueId, IssueType issueType, Status status);
}