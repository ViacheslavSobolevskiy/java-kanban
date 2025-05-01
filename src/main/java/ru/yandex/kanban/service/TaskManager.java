package ru.yandex.kanban.service;

import lombok.NonNull;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.util.List;

public interface TaskManager {
    /**
     * Класс TaskManager станет интерфейсом. В нём нужно собрать список методов, которые должны
     * быть у любого объекта-менеджера. Вспомогательные методы, если вы их создавали, переносить
     * в интерфейс не нужно.
     * <p>
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     * Методы для каждого из типа задач (Задача/Эпик/Подзадача)
     */
    // a. Получение списка всех задач.
    List<Task> getAllTasks();

    // b. Удаление всех задач.
    void removeAllTasks();

    // c. Получение по идентификатору.
    Task getTaskById(Integer taskId);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    Integer createTask(@NonNull Task task);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(@NonNull Task task);

    // f. Удаление по идентификатору.
    void removeTaskById(Integer taskId);

    // a. Получение списка всех задач.
    List<Epic> getAllEpics();

    // b. Удаление всех задач.
    void removeAllEpics();

    // c. Получение по идентификатору.
    Epic getEpicById(Integer epicId);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    Integer createEpic(@NonNull Epic epic);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(@NonNull Epic epic);

    // f. Удаление по идентификатору.
    void removeEpicById(Integer epicId);

    // a. Получение списка всех задач.
    List<Subtask> getAllSubtasks();

    // b. Удаление всех задач.
    void removeAllSubtasks();

    // c. Получение по идентификатору.
    Subtask getSubtaskById(Integer subtaskId);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    Integer createSubtask(@NonNull Subtask subtask);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateSubtask(@NonNull Subtask subtask);

    // f. Удаление по идентификатору.
    void removeSubtaskById(Integer subtaskId);

    // Дополнительные методы:
    // a. Получение списка всех подзадач определённого эпика.
    List<Subtask> getAllSubtasksByEpicId(Integer epicId);

    // Получение списка последних просмотренных задач
    List<Task> getHistory();
}
