package ru.yandex.tasktracker.service;

import org.jetbrains.annotations.NotNull;
import ru.yandex.tasktracker.issue.Epic;
import ru.yandex.tasktracker.issue.Status;
import ru.yandex.tasktracker.issue.Subtask;
import ru.yandex.tasktracker.issue.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int uniqueId = 0;
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(@NotNull HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private static int nextUniqueId() {
        return ++uniqueId;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public int createTask(@NotNull Task task) {
        int newId = nextUniqueId();
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    @Override
    public int createSubtask(@NotNull Subtask subtask) {
        int epicId = subtask.getEpicId();

        if (epics.containsKey(epicId)) {
            int newId = nextUniqueId();
            subtask.setId(newId);
            subtasks.put(newId, subtask);
            epics.get(epicId).addSubtaskId(newId);
            updateEpicStatus(epicId);
        } else {
            System.out.println("Ошибка createSubtask: Epic not found " + epicId);
        }
        return -1;
    }

    @Override
    public int createEpic(@NotNull Epic epic) {
        int newId = nextUniqueId();
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId))
            tasks.remove(taskId);
        else
            System.out.println("Ошибка removeTask: Task not found " + taskId);
    }

    @Override
    public void removeSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(subtaskId);
                updateEpicStatus(epic.getId());
            } else
                System.out.println("Ошибка removeIssue: Subtask not found " + subtaskId);
        }
        subtasks.remove(subtaskId);
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            for (int subtaskId : epics.get(epicId).getSubtaskIds())
                subtasks.remove(subtaskId);
            epics.remove(epicId);
        } else
            System.out.println("Ошибка removeIssue: Epic not found " + epicId);
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return tasks.get(taskId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epics.get(epicId);
    }

    @Override
    public void deleteAllTasks() {
        for (int taskId : tasks.keySet())
            removeTask(taskId);
    }

    @Override
    public void deleteAllSubtasks() {
        for (int subtaskId : subtasks.keySet())
            removeSubtask(subtaskId);
    }

    @Override
    public void deleteAllEpics() {
        for (int epicId : epics.keySet())
            removeEpic(epicId);
    }

    @Override
    public Set<Task> getAllTasks() {
        return new HashSet<>(tasks.values());
    }

    @Override
    public Set<Subtask> getAllSubtasks() {
        return new HashSet<>(subtasks.values());
    }

    @Override
    public Set<Epic> getAllEpics() {
        return new HashSet<>(epics.values());
    }

    @Override
    public Set<Subtask> getAllSubtasksByEpicId(int epicId) {
        if (epics.containsKey(epicId)) {
            Set<Subtask> subtasksNew = new HashSet<>();
            for (int subtaskId : epics.get(epicId).getSubtaskIds())
                subtasksNew.add(subtasks.get(subtaskId));
            return subtasksNew;
        } else
            return Collections.emptySet();
    }

    @Override
    public void updateTask(int taskId, @NotNull Task task) {
        if (tasks.containsKey(taskId))
            tasks.put(taskId, task);
    }

    @Override
    public void updateSubtask(int subtaskId, @NotNull Subtask subtask) {
        if (!subtasks.containsKey(subtaskId)) {
            System.out.println("Ошибка updateSubtask: Subtask not found " + subtaskId);
            return;
        }

        int newEpicId = subtask.getEpicId();

        if (epics.containsKey(newEpicId)) {
            removeSubtask(subtaskId);
            subtasks.put(subtaskId, subtask);
            epics.get(newEpicId).addSubtaskId(subtaskId);
            updateEpicStatus(newEpicId);
        } else
            System.out.println("Ошибка updateSubtask: Epic not found " + newEpicId);
    }

    @Override
    public void updateEpic(int epicId, @NotNull Epic epic) {
        if (epics.containsKey(epicId)) {
            removeEpic(epicId);
            epics.put(epicId, epic);
        } else
            System.out.println("Ошибка updateEpic: Epic not found " + epicId);
    }

    public void updateTaskStatus(int taskId, @NotNull Status status) {
        if (tasks.containsKey(taskId))
            tasks.get(taskId).setStatus(status);
    }

    public void updateSubtaskStatus(int subtaskId, @NotNull Status status) {
        if (subtasks.containsKey(subtaskId)) {
            subtasks.get(subtaskId).setStatus(status);
            int epicId = subtasks.get(subtaskId).getEpicId();
            updateEpicStatus(epicId);
        }
    }

    public void updateEpicStatus(int epicId, @NotNull Status status) {
        if (epics.containsKey(epicId)) {
            epics.get(epicId).setStatus(status);
            switch (status) {
                case Status.IN_PROGRESS -> {
                    for (int subtaskId : epics.get(epicId).getSubtaskIds())
                        updateSubtaskStatus(subtaskId, Status.IN_PROGRESS);
                }
                case Status.DONE -> {
                    for (int subtaskId : epics.get(epicId).getSubtaskIds())
                        updateSubtaskStatus(subtaskId, Status.DONE);
                }
                case Status.NEW -> {
                    for (int subtaskId : epics.get(epicId).getSubtaskIds())
                        subtasks.get(subtaskId).setStatus(Status.NEW);
                }
                default -> System.out.println("Ошибка setEpicStatus: Epic status not set "
                        + epicId);
            }
        } else
            System.out.println("Ошибка setEpicStatus: Epic not found " + epicId);
        updateEpicStatus(epicId);
    }

    public void printTask(int taskId) {
        if (tasks.containsKey(taskId))
            System.out.println(tasks.get(taskId));
        else
            System.out.println("Ошибка printTask: Task not found " + taskId);
    }

    public void printSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId))
            System.out.println(subtasks.get(subtaskId));
        else
            System.out.println("Ошибка printTask: Subtask not found " + subtaskId);
    }

    public void printEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            System.out.println(epics.get(epicId));

            for (int subtaskId : epics.get(epicId).getSubtaskIds()) {
                System.out.println("--> " + subtasks.get(subtaskId));
            }
        } else
            System.out.println("Ошибка printEpic: Epic not found " + epicId);
    }

    public void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : tasks.values()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : epics.values()) {
            System.out.println(epic);

            for (Integer subtaskId : epics.get(epic.getId()).getSubtaskIds()) {
                System.out.println("--> " + subtasks.get(subtaskId));
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask);
        }
    }

    public void printAllSubtasks() {
        for (int subtaskId : subtasks.keySet())
            printSubtask(subtaskId);
    }

    public void printAllEpics() {
        for (int epicId : epics.keySet())
            printEpic(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void printHistory() {
        System.out.println("История задач:");
        for (Task task : getHistory()) {
            int id = task.getId();
            if (subtasks.containsKey(id))
                printSubtask(id);
            else if (epics.containsKey(id))
                printEpic(id);
            else if (tasks.containsKey(id))
                printTask(id);
            else
                System.out.println("Ошибка printHistory: Task not found " + id);
        }
    }

    public void reset() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        InMemoryTaskManager.uniqueId = 0;
        InMemoryHistoryManager.reset();
    }

    public void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            if (epic.getSubtaskIds().isEmpty())
                epic.setStatus(Status.NEW);
            else {
                int doneCount = 0;
                int newCount = 0;

                for (int subtaskId : epic.getSubtaskIds())
                    switch (subtasks.get(subtaskId).getStatus()) {
                        case DONE -> doneCount++;
                        case NEW -> newCount++;
                        case IN_PROGRESS -> epic.setStatus(Status.IN_PROGRESS);
                        default -> System.out.println("Ошибка updateEpicStatus: Subtask not found "
                                + subtaskId);
                    }

                if (doneCount == epic.getSubtaskIds().size())
                    epic.setStatus(Status.DONE);
                else if (newCount == epic.getSubtaskIds().size())
                    epic.setStatus(Status.NEW);
                else
                    epic.setStatus(Status.IN_PROGRESS);
            }
        } else
            System.out.println("Ошибка updateEpicStatus: Epic not found " + epicId);
    }
}
