package ru.yandex.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.issue.Epic;
import ru.yandex.tasktracker.issue.Status;
import ru.yandex.tasktracker.issue.Subtask;
import ru.yandex.tasktracker.issue.Task;
import ru.yandex.tasktracker.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    public static Managers managers = new Managers();
    public static TaskManager taskManager = managers.getDefault();

    @BeforeEach
    void setUp() {
        taskManager.reset();
    }

    @Test
    void reset() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", "Subtask-3",
                Status.NEW, 1));

        taskManager.reset();

        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void add() {
        Epic epic = new Epic("Epic-1", "Epic-1");
        taskManager.getHistoryManager().add(epic);

        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void getHistory() {
        Epic epic = new Epic("Epic-1", "Epic-1");
        taskManager.getHistoryManager().add(epic);

        List<Task> history = taskManager.getHistory();
        assertTrue(history.equals(List.of(epic)));
    }
}