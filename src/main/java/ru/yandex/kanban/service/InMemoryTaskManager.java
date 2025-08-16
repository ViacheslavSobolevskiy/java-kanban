package ru.yandex.kanban.service;

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

    protected void refreshEpicStatusById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка refreshEpicStatusById: Epic не найден " + epicId);

        var subtaskIds = epic.getDependentSubtaskIds();
        if (subtaskIds == null || subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        var allDone = subtaskIds.stream()
                .map(subtasks::get)
                .allMatch(subtask -> subtask != null && subtask.getStatus() == Status.DONE);
        if (allDone) {
            epic.setStatus(Status.DONE);
            return;
        }

        var anyInProgress = subtaskIds.stream()
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
    public void updateTask(Task task) {
        if (task.getId() == null)
            throw new IllegalArgumentException("Ошибка updateTask: Task не имеет идентификатора");

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        var subtaskId = subtask.getId();
        if (subtaskId == null)
            throw new IllegalArgumentException("Ошибка updateSubtask: Subtask не имеет идентификатора");

        var epicId = subtask.getEpicId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка updateSubtask: Subtask не имеет идентификатора Epic");
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка updateSubtask: Epic " + epicId +
                    "не найден для Subtask " + subtaskId);

        subtasks.put(subtaskId, subtask);
        refreshEpicStatusById(epicId);
    }

    @Override
    public void updateEpic(Epic epic) {
        var epicId = epic.getId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка updateEpic: Epic не имеет идентификатора");

        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeTaskById(int taskId) {
        var task = tasks.remove(taskId);
        if (task == null)
            throw new IllegalArgumentException("Ошибка removeTask: Task не найден " + taskId);

        historyManager.remove(taskId);

    }

    public void removeSubtaskById(int subtaskId) {
        var subtask = subtasks.remove(subtaskId);
        if (subtask == null)
            throw new IllegalArgumentException("Ошибка removeSubtask: Subtask не найден " + subtaskId);

        var epicId = subtask.getEpicId();
        var epic = epics.get(epicId);
        if (epic == null)
            throw new RuntimeException("Ошибка removeSubtask: Epic не найден для подзадачи " + subtaskId);

        epic.removeSubtaskId(subtaskId);
        refreshEpicStatusById(epicId);

        historyManager.remove(subtaskId);

    }

    @Override
    public void removeEpicById(int epicId) {
        var epic = epics.remove(epicId);
        if (epic == null)
            throw new IllegalArgumentException("Ошибка removeEpic: Epic не найден " + epicId);

        // Удаляем связанные подзадачи из хранилища и истории
        epic.getDependentSubtaskIds().forEach(id -> {
            subtasks.remove(id);
            historyManager.remove(id);
        });

        // Удаляем сам эпик из истории
        historyManager.remove(epicId);

    }

    @Override
    public Task getTaskById(int taskId) {
        var task = tasks.get(taskId);
        if (task == null)
            throw new IllegalArgumentException("Ошибка getTask: Task не найден " + taskId);

        historyManager.add(task.clone());

        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        var subtask = subtasks.get(subtaskId);
        if (subtask == null)
            throw new IllegalArgumentException("Ошибка getSubtask: Subtask не найден " + subtaskId);

        historyManager.add(subtask.clone());

        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null && epic.getId() != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public int createTask(Task task) {
        var taskId = task.getId();
        if (taskId != null)
            throw new IllegalArgumentException("Ошибка createTask: Task уже имеет идентификатор");

        var newId = generateUniqueId();
        task.setId(newId);
        tasks.put(newId, task.clone());

        return newId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        var subtaskId = subtask.getId();
        if (subtaskId != null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask уже имеет идентификатор");

        var epicId = subtask.getEpicId();
        if (epicId == null)
            throw new IllegalArgumentException("Ошибка createSubtask: Subtask не имеет идентификатора Epic");
        if (!epics.containsKey(subtask.getEpicId()))
            throw new IllegalArgumentException("Ошибка createSubtask: Epic не найден " + epicId);

        var newId = generateUniqueId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        epics.get(epicId).addSubtaskId(newId);
        refreshEpicStatusById(epicId);

        return newId;
    }

    @Override
    public int createEpic(Epic epic) {
        var epicId = epic.getId();
        if (epicId != null)
            throw new IllegalArgumentException("Ошибка createEpic: Epic уже имеет идентификатор");

        var newId = generateUniqueId();
        epic.setId(newId);
        epics.put(newId, epic);

        return newId;
    }

    public void removeAllTasks() {
        // Удаляем все задачи и их просмотры из истории
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    public void removeAllSubtasks() {
        // Удаляем подзадачи из истории
        subtasks.keySet().forEach(historyManager::remove);

        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getDependentSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public void removeAllEpics() {
        // Удаляем все подзадачи и эпики из истории
        subtasks.keySet().forEach(historyManager::remove);
        epics.keySet().forEach(historyManager::remove);

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
        var epic = epics.get(epicId);
        if (epic == null)
            throw new IllegalArgumentException("Ошибка getAllSubtasksByEpicId: Epic не найден " + epicId);

        var dependentSubtaskIds = epic.getDependentSubtaskIds();
        if (dependentSubtaskIds == null)
            return new ArrayList<>();

        return dependentSubtaskIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::clone)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
