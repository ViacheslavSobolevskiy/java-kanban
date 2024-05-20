package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.issue.*;

import java.util.*;

public class TaskManager implements ITaskManager {
    private static Integer id = 0;

    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    public TaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private Integer nextId() {
        return ++id;
    }

    public Integer createIssue(Issue issue) {
        if (issue instanceof Epic) {
            Epic epic = (Epic) issue;
            Integer newId = nextId();
            epics.put(newId, epic);
            System.out.println("Создан Epic " + newId);
            return newId;
        } else if (issue instanceof Task) {
            Task task = (Task) issue;
            Integer newId = nextId();
            tasks.put(newId, task);
            System.out.println("Создан Task " + newId);
            return newId;
        } else if (issue instanceof SubTask) {
            SubTask subTask = (SubTask) issue;
            Integer epicId = subTask.getEpicId();

            if (epics.containsKey(epicId)) {
                Integer newId = nextId();
                subTasks.put(newId, subTask);
                epics.get(epicId).addSubTaskId(newId);
                updateIssueStatus(epicId, IssueType.EPIC);
                System.out.println("Создан SubTask " + newId);
                return newId;
            } else {
                System.out.println("Ошибка createIssue: регистрация subtask " + "не возможна. " + "Заданный Epic " + epicId + " не найден.");
                return -1;
            }
        } else {
            System.out.println("Ошибка: неизвестный тип задачи.");
        }
        return -1;
    }

    public void removeIssue(Integer issueId, IssueType issueType) {
        if (issueType == IssueType.TASK) {
            if (tasks.containsKey(issueId)) tasks.remove(issueId);
            else System.out.println("Ошибка removeIssue: Task not found " + issueId);
        } else if (issueType == IssueType.EPIC) {
            if (epics.containsKey(issueId)) {
                for (Integer subtaskId : epics.get(issueId).getSubTaskIds())
                    subTasks.remove(subtaskId);
                epics.remove(issueId);
            } else System.out.println("Ошибка removeIssue: Epic not found " + issueId);
        } else if (issueType == IssueType.SUBTASK) {
            if (subTasks.containsKey(issueId)) {
                Epic epic = epics.get(subTasks.get(issueId).getEpicId());
                if (epic != null) {
                    epic.removeSubTaskId(issueId);
                    updateIssueStatus(issueId, IssueType.EPIC);
                } else System.out.println("Ошибка removeIssue: Subtask not found " + issueId);
            }
            subTasks.remove(issueId);
        }
    }

    public void deleteAllIssuesByType(IssueType type) {
        if (type == null) {
            System.out.println("Ошибка deleteAllIssueByType: задача не задана.");
        } else if (type == IssueType.TASK) {
            for (Integer taskId : tasks.keySet())
                removeIssue(taskId, IssueType.TASK);
        } else if (type == IssueType.SUBTASK) {
            for (Integer subtaskId : subTasks.keySet())
                removeIssue(subtaskId, IssueType.SUBTASK);
        } else if (type == IssueType.EPIC) {
            for (Integer epicId : epics.keySet())
                removeIssue(epicId, IssueType.EPIC);
        } else {
            System.out.println("Ошибка deleteAllIssueByType: тип задачи не задан.");
        }
    }

    public Issue getIssueById(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.add(id, tasks.get(id));
            return tasks.get(id);
        } else if (subTasks.containsKey(id)) {
            historyManager.add(id, subTasks.get(id));
            return subTasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.add(id, epics.get(id));
            return epics.get(id);
        } else {
            System.out.println("Ошибка getIssue: задача не найдена");
            return null;
        }
    }

    public Set<Issue> getAllByType(IssueType type) {
        if (type == null) {
            System.out.println("Ошибка getAllByType: тип задачи не задан.");
            return Collections.emptySet();
        } else if (type == IssueType.TASK) {
            if (tasks.size() == 0) {
                System.out.println("getAllTasks: Task list is empty");
                return Collections.emptySet();
            }
            return new HashSet<>(tasks.values());
        } else if (type == IssueType.SUBTASK) {
            if (subTasks.size() == 0) {
                System.out.println("getAllTasks: Subtasks list is empty");
                return Collections.emptySet();
            }
            return new HashSet<>(subTasks.values());
        } else if (type == IssueType.EPIC) {
            if (subTasks.size() == 0) {
                System.out.println("Subtasks list is empty");
                return Collections.emptySet();
            }
            return new HashSet<>(subTasks.values());
        }
        return Collections.emptySet();
    }

    public Set<SubTask> getAllSubTasksByEpicId(Integer id) {
        if (epics.containsKey(id)) {
            Set<SubTask> subTasksNew = new HashSet<>();
            for (Integer subtaskId : epics.get(id).getSubTaskIds())
                subTasksNew.add(subTasks.get(subtaskId));
            return subTasksNew;
        } else {
            return Collections.emptySet();
        }
    }

    public void setIssueStatus(Integer issueId, IssueType issueType, Status status) {
        if (issueType == IssueType.EPIC) {
            epics.get(issueId).setStatus(status);
            if (status == Status.DONE) {
                for (Integer subtaskId : epics.get(issueId).getSubTaskIds())
                    subTasks.get(subtaskId).setStatus(Status.DONE);
            } else if (status == Status.NEW) {
                for (Integer subtaskId : epics.get(issueId).getSubTaskIds())
                    subTasks.get(subtaskId).setStatus(Status.NEW);
            }
        } else if (issueType == IssueType.SUBTASK) {
            subTasks.get(issueId).setStatus(status);
            Integer epicId = subTasks.get(issueId).getEpicId();
            updateIssueStatus(epicId, IssueType.EPIC);
        } else if (issueType == IssueType.TASK) {
            tasks.get(issueId).setStatus(status);
        } else {
            System.out.println("Ошибка setIssueStatus: Epic not found " + issueId);
        }
    }

    public void updateIssueStatus(Integer issueId, IssueType issueType) {
        if (issueType == IssueType.EPIC) {
            if (epics.containsKey(issueId)) {
                Epic epic = epics.get(issueId);
                if (epic == null) {
                    System.out.println("Ошибка обновления статуса: Epic not found " + issueId);
                    return;
                }
                if (epic.getSubTaskIds().isEmpty()) {
                    epic.setStatus(Status.NEW);
                } else {
                    int doneCount = 0;
                    int newCount = 0;

                    for (Integer subTaskId : epic.getSubTaskIds()) {
                        if (subTasks.get(subTaskId).getStatus() == Status.DONE)
                            doneCount++;
                        if (subTasks.get(subTaskId).getStatus() == Status.NEW)
                            newCount++;
                        if (subTasks.get(subTaskId).getStatus() == Status.IN_PROGRESS) {
                            epic.setStatus(Status.IN_PROGRESS);
                            return;
                        }
                    }

                    if (doneCount == epic.getSubTaskIds().size()) {
                        epic.setStatus(Status.DONE);
                    } else if (newCount == epic.getSubTaskIds().size()) {
                        epic.setStatus(Status.NEW);
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                    }
                }
            } else {
                System.out.println("Ошибка обновления статуса: Epic not found " + issueId);
            }
        } else if (issueType == IssueType.SUBTASK) {
            SubTask subTask = subTasks.get(issueId);
            if (subTask == null) {
                System.out.println("Ошибка обновления статуса: Subtask not found " + issueId);
                return;
            }
            Integer epicId = subTask.getEpicId();
            updateIssueStatus(epicId, IssueType.EPIC);
        } else {
            System.out.println("Ошибка обновления статуса: Type not found");
        }
    }

    public void printIssue(Integer issueId, IssueType issueType) {
        if (issueType == IssueType.TASK) {
            System.out.println("Task{" + "id=" + issueId + ", name=" + tasks.get(issueId).getName() + ", description=" + tasks.get(issueId).getDescription() + ", status=" + tasks.get(issueId).getStatus() + '}');
        } else if (issueType == IssueType.SUBTASK) {
            System.out.println("Subtask{" + "id=" + issueId + ", epicId=" + subTasks.get(issueId).getEpicId() + ", name=" + subTasks.get(issueId).getName() + ", description=" + subTasks.get(issueId).getDescription() + ", status=" + subTasks.get(issueId).getStatus() + '}');
        } else if (issueType == IssueType.EPIC) {
            System.out.println("Epic{" + "id=" + issueId + ", subtasksIds=" + epics.get(issueId).getSubTaskIds() + ", name=" + epics.get(issueId).getName() + ", description=" + epics.get(issueId).getDescription() + ", status=" + epics.get(issueId).getStatus() + '}');
        } else {
            System.out.println("Ошибка printIssue: тип задачи не задан.");
        }
    }

    public void printAllIssuesByType(IssueType issuetype) {
        if (issuetype == null) {
            System.out.println("Ошибка printIssueByType: тип задачи не задан.");
        } else if (issuetype == IssueType.TASK) {
            for (Integer issueId : tasks.keySet())
                printIssue(issueId, IssueType.TASK);
        } else if (issuetype == IssueType.SUBTASK) {
            for (Integer issueId : subTasks.keySet())
                printIssue(issueId, IssueType.SUBTASK);
        } else if (issuetype == IssueType.EPIC) {
            for (Integer issueId : epics.keySet()) {
                printIssue(issueId, IssueType.EPIC);
            }
        } else {
            System.out.println("Ошибка printIssueByType: тип задачи не задан.");
        }
    }

    public Set<Issue> getHistory() {
        return historyManager.getHistory();
    }
}

