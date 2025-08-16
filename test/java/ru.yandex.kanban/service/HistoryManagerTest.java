package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.utility.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        // В истории должна остаться только последняя версия задачи
        assertEquals(1, history.size());

        assertEquals("Новое имя", history.getFirst().getName());
        assertEquals("Новое описание", history.getFirst().getDescription());
        assertEquals(Status.DONE, history.getFirst().getStatus());
    }

    @Test
    void add() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void addMultipleTasks_preservesOrder() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        Task task2 = new Task(2, "task2", "desc2", Status.IN_PROGRESS);
        Task task3 = new Task(3, "task3", "desc3", Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
        assertEquals(3, history.get(2).getId());
    }

    @Test
    void addDuplicateTask_movesToEnd() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        Task task2 = new Task(2, "task2", "desc2", Status.IN_PROGRESS);
        Task task3 = new Task(3, "task3", "desc3", Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1); // Повторное добавление task1

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(2, history.get(0).getId()); // task2 теперь первый
        assertEquals(3, history.get(1).getId()); // task3 второй
        assertEquals(1, history.get(2).getId()); // task1 переместился в конец
    }

    @Test
    void remove_existingTask() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        Task task2 = new Task(2, "task2", "desc2", Status.IN_PROGRESS);
        Task task3 = new Task(3, "task3", "desc3", Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Удаляем task2

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
    }

    @Test
    void remove_nonExistingTask() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        historyManager.add(task1);

        historyManager.remove(999); // Удаляем несуществующую задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(1, history.getFirst().getId());
    }

    @Test
    void remove_fromHead() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        Task task2 = new Task(2, "task2", "desc2", Status.IN_PROGRESS);
        Task task3 = new Task(3, "task3", "desc3", Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1); // Удаляем первый элемент

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
    }

    @Test
    void remove_fromTail() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        Task task2 = new Task(2, "task2", "desc2", Status.IN_PROGRESS);
        Task task3 = new Task(3, "task3", "desc3", Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3); // Удаляем последний элемент

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
    }

    @Test
    void remove_singleElement() {
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        historyManager.add(task1);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_emptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void addTaskWithNullId_throwsException() {
        Task task = new Task(null, "task", "desc", Status.NEW);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                historyManager.add(task)
        );

        assertEquals("Task id не должен быть null.", exception.getMessage());
    }
}
