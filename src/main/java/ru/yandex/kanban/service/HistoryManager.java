package ru.yandex.kanban.service;

import lombok.NonNull;
import ru.yandex.kanban.issue.Task;

import java.util.List;

/**
 * Создайте отдельный интерфейс для управления историей просмотров — HistoryManager.
 * У него будет два метода: add(Task task) должен помечать задачи как просмотренные,
 * а getHistory — возвращать их список.
 */

public interface HistoryManager {
    void add(@NonNull Task task);
    List<Task> getHistory();
}
