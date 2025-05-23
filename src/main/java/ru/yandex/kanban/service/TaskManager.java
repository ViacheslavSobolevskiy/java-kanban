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
    Task getTaskById(int taskId);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    int createTask(@NonNull Task task);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(@NonNull Task task);

    // f. Удаление по идентификатору.
    Task removeTaskById(int taskId);

    // a. Получение списка всех задач.
    List<Epic> getAllEpics();

    // b. Удаление всех задач.
    void removeAllEpics();

    // c. Получение по идентификатору.
    Epic getEpicById(int epicId);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    int createEpic(@NonNull Epic epic);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(@NonNull Epic epic);

    // f. Удаление по идентификатору.
    Epic removeEpicById(int epicId);

    // a. Получение списка всех задач.
    List<Subtask> getAllSubtasks();

    // b. Удаление всех задач.
    void removeAllSubtasks();

    // c. Получение по идентификатору.
    Subtask getSubtaskById(int subtaskId);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    int createSubtask(@NonNull Subtask subtask);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateSubtask(@NonNull Subtask subtask);

    // f. Удаление по идентификатору.
    Subtask removeSubtaskById(int subtaskId);

    // Дополнительные методы:
    // a. Получение списка всех подзадач определённого эпика.
    List<Subtask> getAllSubtasksByEpicId(int epicId);

    // Получение списка последних просмотренных задач
    List<Task> getHistory();
}
