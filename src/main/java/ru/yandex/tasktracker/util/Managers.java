package ru.yandex.tasktracker.util;

import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.TaskManager;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager defaultHistory) {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    private Managers() {
    }
}
