package ru.yandex.tasktracker.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    static HistoryManager mockHistoryManager;

    @BeforeEach
    public void setUp() {
        mockHistoryManager = Managers.getDefaultHistory();
    }

    @Test
    public void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault(mockHistoryManager);
        assertNotNull(taskManager);
    }

    @Test
    public void testGetDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    public void testGetDefaultTaskManager_withCustomHistoryManager() {
        TaskManager taskManager = Managers.getDefault(mockHistoryManager);
        assertNotNull(taskManager);
    }
}