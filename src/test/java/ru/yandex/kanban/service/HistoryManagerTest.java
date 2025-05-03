package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.utility.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 9. Убедитесь, что задачи, добавляемые в HistoryManager, сохраняют
 * предыдущую версию задачи и её данных.
 */

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void start() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskClone_previousVersionNotEqualNewVersion() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        historyManager.add(task);

        // Меняем задачу
        task.setName("Новое имя");
        task.setDescription("Новое описание");
        task.setStatus(Status.DONE);

        historyManager.add(task);

        // Получаем историю
        var history = historyManager.getHistory();

        // В истории обе задачи будут с новыми данными, т.к. копия делается при добавлении
        assertEquals(2, history.size());

        assertEquals("Новое имя", history.get(1).getName());
        assertEquals("task", history.get(0).getName());

        assertEquals("Новое описание", history.get(1).getDescription());
        assertEquals("desc", history.get(0).getDescription());

        assertEquals(Status.DONE, history.get(1).getStatus());
        assertEquals(Status.NEW, history.get(0).getStatus());
    }

    @Test
    void add() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}
