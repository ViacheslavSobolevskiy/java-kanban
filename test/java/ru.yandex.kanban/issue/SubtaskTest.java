package ru.yandex.kanban.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.service.TaskManager;
import ru.yandex.kanban.utility.Managers;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 4.проверьте, что объект Subtask нельзя сделать своим же эпиком;
 */

class SubtaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void start() {
        taskManager = Managers.getDefault();
    }

    @Test
    void impossibleCreateSubtaskWithEpicAsItself() {
        Epic epic = new Epic("epic", "epic");
        Integer epicId = taskManager.createEpic(epic);

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.createSubtask(new Subtask(epicId, epicId,
                        "subtask-1", "subtask-1", Status.NEW)));
    }
}
