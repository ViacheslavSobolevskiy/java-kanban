package ru.yandex.kanban.service;

import lombok.NonNull;
import lombok.val;
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

public class InMemoryTaskManager implements TaskManager {
    private final AtomicInteger uniqueId = new AtomicInteger();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // Добавлена читаемость
    private void refreshEpicStatusById(Integer epicId) {
        val epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка refreshEpicStatusById: Epic не найден " + epicId);

        val subtaskIds = epic.getDependentSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        val allDone = subtaskIds.stream()
                .map(subtasks::get)
                .allMatch(subtask -> subtask != null && subtask.getStatus() == Status.DONE);
        if (allDone) {
            epic.setStatus(Status.DONE);
            return;
        }

        val anyInProgress = subtaskIds.stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask != null && subtask.getStatus() == Status.IN_PROGRESS);
        if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }

        epic.setStatus(Status.NEW);
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
        val subtaskId = subtask.getId();
        if (subtaskId == null)
            throw new IllegalArgumentException("Ошибка updateSubtask: Subtask не имеет идентификатора");

        val epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка updateSubtask: Epic " + epicId +
                    "не найден для Subtask " + subtaskId);

        subtasks.put(subtaskId, subtask);
        refreshEpicStatusById(epicId);
    }

    @Override
    public void updateEpic(@NonNull Epic epic) {
        val epicId = epic.getId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка updateEpic: Epic не имеет идентификатора");

        epics.put(epic.getId(), epic);
    }

    @Override
    public Task removeTaskById(int taskId) {
        val task = tasks.remove(taskId);
        if (task == null)
            throw new IllegalArgumentException("Ошибка removeTask: Task не найден " + taskId);

        return task;
    }

    public Subtask removeSubtaskById(int subtaskId) {
        val subtask = subtasks.remove(subtaskId);
        if (subtask == null)
            throw new IllegalArgumentException("Ошибка removeSubtask: Subtask не найден " + subtaskId);

        val epicId = subtask.getEpicId();
        val epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка removeSubtask: Epic не найден для подзадачи " + subtaskId);

        epic.removeSubtaskId(subtaskId);
        refreshEpicStatusById(epicId);

        return subtask;
    }

    @Override
    public Epic removeEpicById(int epicId) {
        val epic = epics.remove(epicId);
        if (epic == null)
            throw new IllegalArgumentException("Ошибка removeEpic: Epic не найден " + epicId);

       epic.getDependentSubtaskIds().forEach(subtasks::remove);

       return epic;
    }

    @Override
    public Task getTaskById(int taskId) {
        val task = tasks.get(taskId);
        if (task == null)
            throw new IllegalArgumentException("Ошибка getTask: Task не найден " + taskId);

        historyManager.add(task.clone());

        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        val subtask = subtasks.get(subtaskId);
        if (subtask == null)
            throw new IllegalArgumentException("Ошибка getSubtask: Subtask не найден " + subtaskId);

        historyManager.add(subtask.clone());

        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        val epic = epics.get(epicId);
        if (epic == null)
            throw new IllegalArgumentException("Ошибка getEpic: Epic не найден " + epicId);

        historyManager.add(epic.clone());

        return epic;
    }

    @Override
    public int createTask(@NonNull Task task) {
        val taskId = task.getId();
        if (taskId != null)
            throw new IllegalArgumentException("Ошибка createTask: Task уже имеет идентификатор");

        val newId = generateUniqueId();
        task.setId(newId);
        tasks.put(newId, task);

        return newId;
    }

    @Override
    public int createSubtask(@NonNull Subtask subtask) {
        val subtaskId = subtask.getId();
        if (subtaskId != null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask уже имеет идентификатор");

        val epicId = subtask.getEpicId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask не имеет идентификатора Epic");
        if (!epics.containsKey(subtask.getEpicId()))
            throw new IllegalArgumentException("Ошибка createSubtask: Epic не найден " + epicId);

        val newId = generateUniqueId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        epics.get(epicId).addSubtaskId(newId);
        refreshEpicStatusById(epicId);

        return newId;
    }

    @Override
    public int createEpic(@NonNull Epic epic) {
        val epicId = epic.getId();
        if (epicId != null)
            throw new IllegalArgumentException("Ошибка createEpic: Epic уже имеет идентификатор");

        val newId = generateUniqueId();
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
    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
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
