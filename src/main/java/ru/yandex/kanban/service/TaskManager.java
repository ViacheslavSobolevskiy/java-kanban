package ru.yandex.kanban.service;

import lombok.NonNull;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.util.List;

public interface TaskManager {
    /**
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    Методы для каждого из типа задач (Задача/Эпик/Подзадача):
    */

    /**
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     * Методы для каждого из типа задач - Задача:
     */
    // a. Получение списка всех задач.
    List<Long> getAllTasks();
    // b. Удаление всех задач.
    void removeAllTasks();
    // c. Получение по идентификатору.
    Task getTaskById(@NonNull Long taskId);
    // d. Создание. Сам объект должен передаваться в качестве параметра.
    void createTask(@NonNull Task task);
    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(@NonNull Task task);
    // f. Удаление по идентификатору.
    void removeTaskById(@NonNull Long taskId);

    /**
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     * Методы для каждого из типа задач - Эпик:
     */
    // a. Получение списка всех задач.
    List<Long> getAllEpics();
    // b. Удаление всех задач.
    void removeAllEpics();
    // c. Получение по идентификатору.
    Epic getEpicById(@NonNull Long epicId);
    // d. Создание. Сам объект должен передаваться в качестве параметра.
    void createEpic(@NonNull Epic epic);
    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(@NonNull Epic epic);
    // f. Удаление по идентификатору.
    void removeEpicById(@NonNull Long epicId);
    // Дополнительные методы:
    // a. Получение списка всех подзадач определённого эпика.
    List<Long> getAllSubtasksByEpicId(@NonNull Long epicId);

    /**
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     * Методы для каждого из типа задач - Подзадача:
     */
    // a. Получение списка всех задач.
    List<Long> getAllSubtasks();
    // b. Удаление всех задач.
    void removeAllSubtasks();
    // c. Получение по идентификатору.
    Subtask getSubtaskById(@NonNull Long subtaskId);
    // d. Создание. Сам объект должен передаваться в качестве параметра.
    void createSubtask(@NonNull Subtask subtask);
    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateSubtask(@NonNull Subtask subtask);
    // f. Удаление по идентификатору.
    void removeSubtaskById(@NonNull Long subtaskId);

    /**
     * ИТОГО: 19 публичных методов
     *
     * В соответствии с этим блоком тз всего в менеджере должно быть 18 публичных методов
     * для модификации задач по типам,
     * 1 публичный метод получения всех задач определенного эпика,
     * Про управление статусами:
     */
}
