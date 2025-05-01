package ru.yandex.kanban.service;

import lombok.NonNull;
import ru.yandex.kanban.issue.Task;
import java.util.LinkedList;
import java.util.List;

/**
 * Объявите класс InMemoryHistoryManager и перенесите в него часть кода для работы с
 * историей из класса InMemoryTaskManager. Новый класс InMemoryHistoryManager должен
 * реализовывать интерфейс HistoryManager.
 */

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIMIT = 10;
    private final LinkedList<Task> historyList = new LinkedList<>();

    @Override
    public void add(@NonNull Task task) {
        historyList.addLast(task.copy());
        if (historyList.size() > HISTORY_LIMIT) {
            historyList.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(historyList);
    }
}
