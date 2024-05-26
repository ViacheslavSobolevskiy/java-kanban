package ru.yandex.tasktracker.utils;

import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.TaskManager;

public class Managers<T extends TaskManager> {

    private T taskManager;

    public HistoryManager getDefaultHistory() {
        return taskManager.getHistoryManager();
    }

    public T getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public Managers(T taskManager) {
    }
}
