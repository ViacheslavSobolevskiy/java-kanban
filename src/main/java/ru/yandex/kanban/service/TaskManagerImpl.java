package ru.yandex.kanban.service;

import lombok.Getter;
import lombok.NonNull;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TaskManagerImpl implements TaskManager {
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();

    public TaskManagerImpl() {
    }

    private void refreshEpicStatusById(@NonNull Epic epic) {
        Set<Long> subtaskIds = epic.getDependentSubtaskIds();

        if (subtaskIds == null || subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Long subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null)
                throw new RuntimeException("Ошибка refreshEpicStatusById: Subtask не найден " + subtaskId);

            Status status = subtask.getStatus();
            if (status != Status.DONE) {
                allDone = false;
            }
            if (status == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void updateTask(@NonNull Task task) {
        if (task.getId() == null)
            throw new RuntimeException("Ошибка updateTask: Task не имеет идентификатора");

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(@NonNull Subtask subtask) {
        Long subtaskId = subtask.getId();
        if (subtaskId == null)
            throw new RuntimeException("Ошибка updateSubtask: Subtask не имеет идентификатора");

        Long epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            throw new RuntimeException("Ошибка updateSubtask: Epic " + epicId +
                    "не найден для Subtask " + subtaskId);
        }

        subtasks.put(subtaskId, subtask);
        epics.get(epicId).updateSubtaskId(subtaskId);
        refreshEpicStatusById(epics.get(epicId));
    }

    @Override
    public void updateEpic(@NonNull Epic epic) {
        if (epic.getId() == null)
            throw new RuntimeException("Ошибка updateEpic: Epic не имеет идентификатора");

        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeTaskById(@NonNull Long taskId) {
        if (!tasks.containsKey(taskId))
            throw new IllegalArgumentException("Ошибка removeTask: Task не найден " + taskId);

        tasks.remove(taskId);
    }

    public void removeSubtaskById(@NonNull Long subtaskId) {
        if (!subtasks.containsKey(subtaskId))
            throw new IllegalArgumentException("Ошибка removeSubtask: Subtask не найден " + subtaskId);

        Subtask subtask = subtasks.get(subtaskId);
        Long epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка removeSubtask: Epic не найден для подзадачи " + subtaskId);

        Epic epic = epics.get(epicId);
        epic.removeSubtaskId(subtaskId);
        refreshEpicStatusById(epic);
        subtasks.remove(subtaskId);
    }

    @Override
    public void removeEpicById(@NonNull Long epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка removeEpic: Epic не найден " + epicId);

        epics.remove(epicId).getDependentSubtaskIds().forEach(subtasks::remove);
    }

    public Task getTaskById(@NonNull Long taskId) {
        if (!tasks.containsKey(taskId))
            throw new IllegalArgumentException("Ошибка getTask: Task не найден " + taskId);

        return tasks.get(taskId);
    }

    @Override
    public Subtask getSubtaskById(@NonNull Long subtaskId) {
        if (!subtasks.containsKey(subtaskId))
            throw new IllegalArgumentException("Ошибка getSubtask: Subtask не найден " + subtaskId);

        return subtasks.get(subtaskId);
    }

    @Override
    public Epic getEpicById(@NonNull Long epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка getEpic: Epic не найден " + epicId);

        return epics.get(epicId);
    }

    @Override
    public void createTask(@NonNull Task task) {
        updateTask(task);
    }

    @Override
    public void createSubtask(@NonNull Subtask subtask) {
        updateSubtask(subtask);
    }

    @Override
    public void createEpic(@NonNull Epic epic) {
        updateEpic(epic);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getDependentSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public void removeAllEpics() {
            subtasks.clear();
            epics.clear();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(@NonNull Long epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка getAllSubtasksByEpicId: Epic не найден " + epicId);

        return epics.get(epicId).getDependentSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    public void updateTaskStatusById(@NonNull Long taskId, @NonNull Status status) {
        if (!tasks.containsKey(taskId))
            throw new IllegalArgumentException("Ошибка updateTaskStatus: Task не найден " + taskId);

        tasks.get(taskId).setStatus(status);
    }

    public void updateSubtaskStatusById(@NonNull Long subtaskId, @NonNull Status status) {
        if (!subtasks.containsKey(subtaskId))
            throw new IllegalArgumentException("Ошибка updateSubtaskStatus: Subtask не найден " + subtaskId);

        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null)
            throw new RuntimeException("Ошибка updateSubtaskStatus: Epic не найден для Subtask " + subtaskId);

        subtask.setStatus(status);
        refreshEpicStatusById(epic);
    }
}
