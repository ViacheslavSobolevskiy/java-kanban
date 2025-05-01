package ru.yandex.kanban.utility;

import ru.yandex.kanban.service.HistoryManager;
import ru.yandex.kanban.service.InMemoryHistoryManager;
import ru.yandex.kanban.service.InMemoryTaskManager;
import ru.yandex.kanban.service.TaskManager;


public final class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Добавьте в служебный класс Managers статический метод HistoryManager getDefaultHistory.
     * Он должен возвращать объект InMemoryHistoryManager — историю просмотров.
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
