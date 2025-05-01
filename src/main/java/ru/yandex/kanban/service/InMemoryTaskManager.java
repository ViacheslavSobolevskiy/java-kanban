package ru.yandex.kanban.service;

import lombok.Getter;
import lombok.NonNull;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.utility.Managers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Созданный ранее класс менеджера нужно переименовать в InMemoryTaskManager. Именно то,
 * что менеджер хранит всю информацию в оперативной памяти, и есть его главное свойство,
 * позволяющее эффективно управлять задачами. Внутри класса должна остаться реализация методов.
 * При этом важно не забыть имплементировать TaskManager, ведь в Java класс должен явно заявить,
 * что он подходит под требования интерфейса.
 * <p>
 * Проверьте, что теперь InMemoryTaskManager обращается к менеджеру истории через интерфейс
 * HistoryManager и использует реализацию, которую возвращает метод getDefaultHistory.
 */

@Getter
public class InMemoryTaskManager implements TaskManager {
    private final AtomicInteger uniqueId = new AtomicInteger();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private void refreshEpicStatusById(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка refreshEpicStatusById: Epic не найден " + epicId);

        Set<Integer> subtaskIds = epic.getDependentSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Integer subtaskId : subtaskIds) {
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

    private void addToHistory(@NonNull Task task) {
        historyManager.add(task);
    }

    private int generateUniqueId() {
        int id;

        do {
            id = uniqueId.getAndIncrement();
        } while (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id));

        return id;
    }

    @Override
    public void updateTask(@NonNull Task task) {
        if (task.getId() == null)
            throw new IllegalArgumentException("Ошибка updateTask: Task не имеет идентификатора");

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(@NonNull Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtaskId == null)
            throw new IllegalArgumentException("Ошибка updateSubtask: Subtask не имеет идентификатора");

        Integer epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка updateSubtask: Epic " + epicId +
                    "не найден для Subtask " + subtaskId);

        subtasks.put(subtaskId, subtask);
        refreshEpicStatusById(epicId);
    }

    @Override
    public void updateEpic(@NonNull Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка updateEpic: Epic не имеет идентификатора");

        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeTaskById(Integer taskId) {
        if (!tasks.containsKey(taskId))
            throw new IllegalArgumentException("Ошибка removeTask: Task не найден " + taskId);

        tasks.remove(taskId);
    }

    public void removeSubtaskById(Integer subtaskId) {
        if (!subtasks.containsKey(subtaskId))
            throw new IllegalArgumentException("Ошибка removeSubtask: Subtask не найден " + subtaskId);

        Subtask subtask = subtasks.get(subtaskId);
        Integer epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка removeSubtask: Epic не найден для подзадачи " + subtaskId);

        Epic epic = epics.get(epicId);
        epic.removeSubtaskId(subtaskId);
        refreshEpicStatusById(epicId);
        subtasks.remove(subtaskId);
    }

    @Override
    public void removeEpicById(Integer epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка removeEpic: Epic не найден " + epicId);

        epics.remove(epicId).getDependentSubtaskIds().forEach(subtasks::remove);
    }

    @Override
    public Task getTaskById(Integer taskId) {
        if (!tasks.containsKey(taskId))
            throw new IllegalArgumentException("Ошибка getTask: Task не найден " + taskId);
        Task task = tasks.get(taskId);
        addToHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        if (!subtasks.containsKey(subtaskId))
            throw new IllegalArgumentException("Ошибка getSubtask: Subtask не найден " + subtaskId);
        Subtask subtask = subtasks.get(subtaskId);
        addToHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка getEpic: Epic не найден " + epicId);
        Epic epic = epics.get(epicId);
        addToHistory(epic);
        return epic;
    }

    @Override
    public Integer createTask(@NonNull Task task) {
        Integer taskId = task.getId();
        if (taskId != null)
            throw new IllegalArgumentException("Ошибка createTask: Task уже имеет идентификатор");

        int newId = generateUniqueId();
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    @Override
    public Integer createSubtask(@NonNull Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtaskId != null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask уже имеет идентификатор");

        Integer epicId = subtask.getEpicId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask не имеет идентификатора Epic");
        if (!epics.containsKey(subtask.getEpicId()))
            throw new IllegalArgumentException("Ошибка createSubtask: Epic не найден " + epicId);

        int newId = generateUniqueId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        epics.get(epicId).addSubtaskId(newId);
        refreshEpicStatusById(epicId);
        return newId;
    }

    @Override
    public Integer createEpic(@NonNull Epic epic) {
        Integer epicId = epic.getId();
        if (epicId != null)
            throw new IllegalArgumentException("Ошибка createEpic: Epic уже имеет идентификатор");

        int newId = generateUniqueId();
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
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
    public List<Subtask> getAllSubtasksByEpicId(Integer epicId) {
        if (!epics.containsKey(epicId))
            throw new IllegalArgumentException("Ошибка getAllSubtasksByEpicId: Epic не найден " + epicId);

        return epics.get(epicId).getDependentSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
