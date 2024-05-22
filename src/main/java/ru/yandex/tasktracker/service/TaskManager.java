package ru.yandex.tasktracker.service;

import org.jetbrains.annotations.NotNull;
import ru.yandex.tasktracker.issue.*;

import java.util.*;

public class TaskManager implements ITaskManager {
    private static Integer uniqueId = 0;

    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    public TaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private Integer nextUniqueId() {
        return ++uniqueId;
    }

    @Override
    public Integer createTask(@NotNull Task task) {
        Integer newId = nextUniqueId();
        task.setId(newId);
        tasks.put(newId, task);
        System.out.println("Создан Task " + newId);
        return newId;
    }

    @Override
    public Integer createSubTask(@NotNull SubTask subTask) {
        Integer epicId = subTask.getEpicId();

        if (epics.containsKey(epicId)) {
            Integer newId = nextUniqueId();
            subTask.setId(newId);
            subTasks.put(newId, subTask);
            epics.get(epicId).addSubTaskId(newId);
            updateEpicStatus(epicId);
            System.out.println("Создан SubTask " + newId);
        } else {
            System.out.println("Ошибка createSubTask: Epic not found " + epicId);
        }
        return -1;
    }

    @Override
    public Integer createEpic(@NotNull Epic epic) {
        Integer newId = nextUniqueId();
        epic.setId(newId);
        epics.put(newId, epic);
        System.out.println("Создан Epic " + newId);
        return newId;
    }

    @Override
    public void removeTask(Integer taskId) {
        if (tasks.containsKey(taskId))
            tasks.remove(taskId);
        else
            System.out.println("Ошибка removeTask: Task not found " + taskId);
    }

    @Override
    public void removeSubTask(Integer subtaskId) {
        if (subTasks.containsKey(subtaskId)) {
            Epic epic = epics.get(subTasks.get(subtaskId).getEpicId());
            if (epic != null) {
                epic.removeSubTaskId(subtaskId);
                updateEpicStatus(epic.getId());
            } else System.out.println("Ошибка removeIssue: Subtask not found " + subtaskId);
        }
        subTasks.remove(subtaskId);
    }

    @Override
    public void removeEpic(Integer epicId) {
        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubTaskIds())
                subTasks.remove(subtaskId);
            epics.remove(epicId);
        } else
            System.out.println("Ошибка removeIssue: Epic not found " + epicId);
    }

    @Override
    public Task getTask(Integer taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return tasks.get(taskId);
    }

    @Override
    public SubTask getSubTask(Integer subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epics.get(epicId);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet())
            removeTask(taskId);
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer subtaskId : subTasks.keySet())
            removeSubTask(subtaskId);
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet())
            removeEpic(epicId);
    }

    @Override
    public Set<Task> getAllTasks() {
        return new HashSet<>(tasks.values());
    }

    @Override
    public Set<SubTask> getAllSubTasks() {
        return new HashSet<>(subTasks.values());
    }

    @Override
    public Set<Epic> getAllEpics() {
        return new HashSet<>(epics.values());
    }

    @Override
    public void setTaskStatus(Integer taskId, @NotNull Status status) {
        if (tasks.containsKey(taskId))
            tasks.get(taskId).setStatus(status);
        else
            System.out.println("Ошибка setTaskStatus: Task not found " + taskId);
    }

    @Override
    public void setSubTaskStatus(Integer subTaskId, @NotNull Status status) {
        if (subTasks.containsKey(subTaskId)) {
            subTasks.get(subTaskId).setStatus(status);
            Integer epicId = subTasks.get(subTaskId).getEpicId();
            updateEpicStatus(epicId);
        } else
            System.out.println("Ошибка setSubTaskStatus: Subtask not found " + subTaskId);
    }

    @Override
    public void setEpicStatus(Integer epicId, @NotNull Status status) {
        if (epics.containsKey(epicId)) {
            epics.get(epicId).setStatus(status);
            if (status == Status.DONE) {
                for (Integer subtaskId : epics.get(epicId).getSubTaskIds())
                    subTasks.get(subtaskId).setStatus(Status.DONE);
            } else if (status == Status.NEW) {
                for (Integer subtaskId : epics.get(epicId).getSubTaskIds())
                    subTasks.get(subtaskId).setStatus(Status.NEW);
            } else
                System.out.println("Ошибка setEpicStatus: Epic not found " + epicId);
        }
    }

    @Override
    public void printTask(Integer taskId) {
        System.out.println("Task{" + "id=" + taskId + ", name=" + tasks.get(taskId).getName() + ", description=" + tasks.get(taskId).getDescription() + ", status=" + tasks.get(taskId).getStatus() + '}');
    }

    @Override
    public void printSubTask(Integer subTaskId) {
        System.out.println("Subtask{" + "id=" + subTaskId + ", epicId=" + subTasks.get(subTaskId).getEpicId() + ", name=" + subTasks.get(subTaskId).getName() + ", description=" + subTasks.get(subTaskId).getDescription() + ", status=" + subTasks.get(subTaskId).getStatus() + '}');
    }

    @Override
    public void printEpic(Integer epicId) {
        System.out.println("Epic{" + "id=" + epicId + ", subtasksIds=" + epics.get(epicId).getSubTaskIds() + ", name=" + epics.get(epicId).getName() + ", description=" + epics.get(epicId).getDescription() + ", status=" + epics.get(epicId).getStatus() + '}');

    }

    @Override
    public void printAllTasks() {
        for (Integer taskId : tasks.keySet())
            printTask(taskId);
    }

    @Override
    public void printAllSubTasks() {
        for (Integer subtaskId : subTasks.keySet())
            printSubTask(subtaskId);
    }

    @Override
    public void printAllEpics() {
        for (Integer epicId : epics.keySet())
            printEpic(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void printHistory() {
        System.out.println("История задач:");
        for (Task task : getHistory()) {
            if (task instanceof SubTask) {
                printSubTask(task.getId());
            } else if (task instanceof Epic) {
                printEpic(task.getId());
            }
                printTask(task.getId());
        }
    }

    public void updateSubTaskStatus(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            updateEpicStatus(subTasks.get(subTaskId).getEpicId());
        } else
            System.out.println("Ошибка updateSubTaskStatus: Subtask not found " + subTaskId);
    }

    public void updateEpicStatus(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка updateEpicStatus: Epic not found " + epicId);
            return;
        }

        Epic epic = epics.get(epicId);
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
    }

    public Set<SubTask> getAllSubTasksByEpicId(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Set<SubTask> subTasksNew = new HashSet<>();
            for (Integer subtaskId : epics.get(epicId).getSubTaskIds())
                subTasksNew.add(subTasks.get(subtaskId));
            return subTasksNew;
        } else {
            return Collections.emptySet();
        }
    }
}

