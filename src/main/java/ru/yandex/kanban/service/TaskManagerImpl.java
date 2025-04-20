package ru.yandex.kanban.service;

import lombok.Getter;
import lombok.NonNull;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Getter
public class TaskManagerImpl implements TaskManager {
    private final AtomicLong uniqueId = new AtomicLong();
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();

    public TaskManagerImpl() {
    }

    private void refreshEpicStatusById(@NonNull Long epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка refreshEpicStatusById: Epic не найден " + epicId);

        Set<Long> subtaskIds = epic.getDependentSubtaskIds();

        if (subtaskIds.isEmpty()) {
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

    private void validateStackConsistency() {
        validateAllKeysUnique();

        if (!subtasks.isEmpty() && epics.isEmpty())
            throw new RuntimeException("Ошибка validateStackConsistency: Subtasks не имеют Epic");

        if (epics.isEmpty())
            return;

        for (Epic epic : epics.values())
            validateEpicConsistencyById(epic.getId());

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == null)
                throw new RuntimeException("Ошибка validateStackConsistency: Subtask " + subtask.getId() +
                        " не имеет идентификатора Epic");
            if (!epics.containsKey(subtask.getEpicId()))
                throw new RuntimeException("Ошибка validateStackConsistency: Subtask " + subtask.getId() +
                        " имеет несуществующий идентификатор Epic " + subtask.getEpicId());
        }
    }

    private void validateEpicConsistencyById(@NonNull Long epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка validateEpicConsistencyById: Epic не найден " + epicId);

        if (epic.getDependentSubtaskIds().isEmpty()) {
            if (epic.getStatus() != Status.NEW)
                throw new RuntimeException("Ошибка validateEpicConsistencyById: Epic " + epicId +
                        " не имеет подзадач и не имеет статуса NEW");
            return;
        }

        for (Long subtaskId : epic.getDependentSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null)
                throw new RuntimeException("Ошибка validateEpicConsistencyById: Subtask не найден " + subtaskId);
            if (!Objects.equals(subtask.getEpicId(), epic.getId()))
                throw new RuntimeException("Ошибка validateEpicConsistencyById: Subtask " + subtaskId +
                        " не принадлежит Epic " + epic.getId());
        }

        int min_status = Status.DONE.ordinal();
        for (Long subtaskId : epic.getDependentSubtaskIds()) {
            int status = subtasks.get(subtaskId).getStatus().ordinal();
            if (status < min_status)
                min_status = status;
        }

        if (epics.get(epicId).getStatus().ordinal() != min_status)
            throw new RuntimeException("Ошибка validateEpicConsistencyById: Epic " + epicId +
                    " имеет несогласованный статус");
    }

    /**
     * Проверяет, что среди всех ключей epics, tasks, subtasks нет пересечений.
     * Если есть дублирующиеся ключи — выбрасывает RuntimeException с описанием.
     */
    private void validateAllKeysUnique() {
        Set<Long> allKeys = new HashSet<>();
        allKeys.addAll(epics.keySet());
        allKeys.addAll(tasks.keySet());
        allKeys.addAll(subtasks.keySet());
        int total = epics.size() + tasks.size() + subtasks.size();
        if (allKeys.size() != total) {
            throw new RuntimeException("Обнаружены неуникальные ключи среди epics, tasks и subtasks");
        }
    }

    @Override
    public void updateTask(@NonNull Task task) {
        if (task.getId() == null)
            throw new IllegalArgumentException("Ошибка updateTask: Task не имеет идентификатора");

        tasks.put(task.getId(), task);
        validateStackConsistency();
    }

    @Override
    public void updateSubtask(@NonNull Subtask subtask) {
        Long subtaskId = subtask.getId();
        if (subtaskId == null)
            throw new IllegalArgumentException("Ошибка updateSubtask: Subtask не имеет идентификатора");

        Long epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка updateSubtask: Epic " + epicId +
                    "не найден для Subtask " + subtaskId);

        subtasks.put(subtaskId, subtask);
        refreshEpicStatusById(epicId);
        validateStackConsistency();
    }

    @Override
    public void updateEpic(@NonNull Epic epic) {
        Long epicId = epic.getId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка updateEpic: Epic не имеет идентификатора");

        epics.put(epic.getId(), epic);
        refreshEpicStatusById(epic.getId());
        validateStackConsistency();
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
        refreshEpicStatusById(epicId);
        subtasks.remove(subtaskId);
        validateStackConsistency();
    }

    @Override
    public void removeEpicById(@NonNull Long epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка removeEpic: Epic не найден " + epicId);

        epics.remove(epicId).getDependentSubtaskIds().forEach(subtasks::remove);
        validateStackConsistency();
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
    public Long createTask(@NonNull Task task) {

        Long taskId = checkIfExists(uniqueId.getAndIncrement());
        task.setId(taskId);
        tasks.put(taskId, task);

        return taskId;
    }

    @Override
    public Long createSubtask(@NonNull Long epicId, @NonNull Subtask subtask) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка createSubtask: Epic не найден " + epicId);
        if (subtask.getEpicId() != null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask уже имеет идентификатор Epic");
        if (subtask.getId() != null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask уже имеет идентификатор");

        Long subtaskId = checkIfExists(uniqueId.getAndIncrement());
        subtask.setId(subtaskId);
        subtask.setEpicId(epicId);
        subtasks.put(subtaskId, subtask);
        epics.get(epicId).addSubtaskId(subtaskId);
        refreshEpicStatusById(epicId);
        validateStackConsistency();

        return subtaskId;
    }

    @Override
    public Long createEpic(@NonNull Epic epic) {
        if (epic.getId() != null)
            throw new IllegalArgumentException("Ошибка createEpic: Epic уже имеет идентификатор");

        Long epicId = checkIfExists(uniqueId.getAndIncrement());
        epic.setId(epicId);
        epics.put(epicId, epic);

        return epicId;
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

    private Long checkIfExists(@NonNull Long id) {
        if (tasks.containsKey(id))
            throw new RuntimeException("Ошибка checkUniqueId: Task с идентификатором уже существует - " + id);

        if (subtasks.containsKey(id))
            throw new RuntimeException("Ошибка checkUniqueId: Subtask с идентификатором уже существует - " + id);

        if (epics.containsKey(id))
            throw new RuntimeException("Ошибка checkUniqueId: Epic с идентификатором уже существует - " + id);

        return id;
    }
}
