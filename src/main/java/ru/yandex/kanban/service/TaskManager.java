package ru.yandex.kanban.service;

import lombok.NonNull;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.util.Set;

public interface TaskManager {
    /**
     * Вносит информацию о Task в TaskManager.
     *
     * @param task - который должен быть обновлен или создан.
     * @return Возвращает уникальный идентификатор обновленной или новой задачи.
     */
    Long updateTask(@NonNull Task task);

    /**
     * Вносит информацию о Subtask в TaskManager.
     *
     * @param subtask - которую нужно обновить.
     * @return Возвращает уникальный идентификатор обновленной подзадачи.
     * @throws IllegalArgumentException если Epic, связанный с Subtask, не найден.
     */
    Long updateSubtask(@NonNull Subtask subtask);

    /**
     * Вносит информацию о Epic в TaskManager.
     * Обновляет существующий Epic или создает новый, если он не существует.
     *
     * @param epic который необходимо обновить или создать.
     * @return Возвращает идентификатор обновленного или нового эпика.
     */
    Long updateEpic(@NonNull Epic epic);

    /**
     * Удаляет Task по ID
     *
     * @param taskId Идентификатор Task
     * @throws IllegalArgumentException если задача с указанным идентификатором не существует
     */
    void removeTaskById(@NonNull Long taskId);

    /**
     * Удаляет подзадачу (Subtask) по её уникальному идентификатору.
     *
     * @param subtaskId идентификатор подзадачи
     * @throws IllegalArgumentException Если подзадача с указанным идентификатором не существует.
     * @throws RuntimeException         Если связанный Epic для подзадачи не найден.
     */
    void removeSubtaskById(@NonNull Long subtaskId);

    /**
     * Удаляет Epic (эпик) по уникальному идентификатору.
     *
     * @param epicId идентификатор эпика
     * @throws IllegalArgumentException Если эпик с указанным идентификатором не существует.
     */
    void removeEpicById(@NonNull Long epicId);

    /**
     * Получения задачи (Task) по её уникальному идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return объект Task
     * @throws IllegalArgumentException Если задача с указанным идентификатором не
     *   существует в TaskManager
     */
    Task getTaskById(@NonNull Long taskId);

    /**
     * Получения Subtask по идентификатору.
     *
     * @param subtaskId идентификатор Subtask
     * @return объект Subtask
     * @throws IllegalArgumentException Если subtaskId не существует в TaskManager
     */
    Subtask getSubtaskById(@NonNull Long subtaskId);

    /**
     * Получения Epic по идентификатору.
     *
     * @param epicId идентификатор Epic
     * @return объект Epic
     * @throws IllegalArgumentException Если epicId не существует в TaskManager
     */
    Epic getEpicById(@NonNull Long epicId);

    /**
     * Удаление всех Task из TaskManager.
     */
    void deleteAllTasks();

    /**
     * Удаление всех Subtask из TaskManager.
     */
    void deleteAllSubtasks();

    /**
     * Удаление всех Epic из TaskManager.
     */
    void deleteAllEpics();

    /**
     * Получение Set идентификаторов всех Task.
     *
     * @return Set уникальных идентификаторов всех Task.
     */
    Set<Long> getAllTasks();

    /**
     * Получение Set идентификаторов всех Subtask.
     *
     * @return Set уникальных идентификаторов всех Subtask.
     */
    Set<Long> getAllSubtasks();

    /**
     * Получение Set идентификаторов всех Epic.
     *
     * @return Set уникальных идентификаторов всех Epic.
     */
    Set<Long> getAllEpics();

    /**
     * Получение Set подзадач, связанные с определённым Epic.
     *
     * @param epicId Идентификатор Epic, для подзадачи.
     * @return Set подзадач, связанных с Epic.
     * @throws IllegalArgumentException Если Epic с указанным идентификатором не существует.
     */
    Set<Long> getAllSubtasksByEpicId(@NonNull Long epicId);

    /**
     * Обновление статуса задачи по ID
     *
     * @param taskId Номер идентификации задачи, для которой нужно обновить статус
     * @param status Новый статус задачи
     */
    void updateTaskStatusById(@NonNull Long taskId, @NonNull Status status);

    /**
     * Обновление статуса Subtask по ID
     */
    void updateSubtaskStatusById(@NonNull Long subtaskId, @NonNull Status status);
}
