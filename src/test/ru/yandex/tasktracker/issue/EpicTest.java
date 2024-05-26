package ru.yandex.tasktracker.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.service.TaskManager;
import ru.yandex.tasktracker.util.Managers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    public static Managers managers = new Managers();
    public static TaskManager taskManager = managers.getDefault();

    @BeforeEach
    void setUp() {
        taskManager.reset();
    }

    @Test
    void getSubtaskIds() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", "Subtask-3",
                Status.NEW, 1));

        Set<Integer> subtaskIds = taskManager.getEpic(1).getSubtaskIds();
        assertEquals(2, subtaskIds.size(), "Subtask-2 and Subtask-3 should exist");
    }

    @Test
    void hasSubtaskId() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", "Subtask-3",
                Status.NEW, 1));

        assertTrue(taskManager.getEpic(1).hasSubtaskId(2)
                        && taskManager.getEpic(1).hasSubtaskId(3),
                "Subtask-2 should exist");
    }

    @Test
    void addSubtaskId() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", "Subtask-3",
                Status.NEW, 1));

        assertTrue(taskManager.getEpic(1).hasSubtaskId(2) &
                        taskManager.getEpic(1).hasSubtaskId(3),
                "Subtask-2 should exist");
    }

    @Test
    void removeSubtaskId() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", "Subtask-3",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-4", "Subtask-4",
                Status.NEW, 1));

        taskManager.removeSubtask(3);

        assertTrue(taskManager.getEpic(1).hasSubtaskId(2),
                "Subtask-2 should exist");
        assertFalse(taskManager.getEpic(1).hasSubtaskId(3),
                "Subtask-2 should not exist");
        assertTrue(taskManager.getEpic(1).hasSubtaskId(4),
                "Subtask-2 should exist");
    }

    @Test
    void emptyEpicStatusShouldBeNew() {
        taskManager.createEpic(new Epic("Epic-1"));
        taskManager.createEpic(new Epic("Epic-2", "Epic-2"));

        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
        assertEquals(Status.NEW, taskManager.getEpic(2).getStatus(),
                "Epic status should be NEW");

        taskManager.setEpicStatus(1, Status.IN_PROGRESS);
        taskManager.setEpicStatus(2, Status.IN_PROGRESS);

        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
        assertEquals(Status.NEW, taskManager.getEpic(2).getStatus(),
                "Epic status should be NEW");

        taskManager.setEpicStatus(1, Status.DONE);
        taskManager.setEpicStatus(2, Status.DONE);

        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
        assertEquals(Status.NEW, taskManager.getEpic(2).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeNew() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", Status.DONE, 1));

        taskManager.removeSubtask(3);

        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeInProgress1() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.IN_PROGRESS, 1));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeInProgress2() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-1", "Subtask-1",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.IN_PROGRESS, 1));
        taskManager.createSubtask(new Subtask("Subtask-3", "Subtask-3",
                Status.DONE, 1));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeInProgress3() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-1", "Subtask-1",
                Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.DONE, 1));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeInProgress4() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-1", "Subtask-1",
                Status.NEW, 1));

        taskManager.createSubtask(new Subtask("Subtask-2", "Subtask-2",
                Status.IN_PROGRESS, 1));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeInProgress5() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-1", "Subtask-1",
                Status.NEW, 1));

        taskManager.setSubtaskStatus(2, Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeDONE() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-1", "Subtask-1",
                Status.DONE, 1));

        assertEquals(Status.DONE, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }

    @Test
    void epicStatusShouldBeDONE2() {
        taskManager.createEpic(new Epic("Epic-1", "Epic-1"));

        taskManager.createSubtask(new Subtask("Subtask-2",
                Status.IN_PROGRESS, 1));
        taskManager.createSubtask(new Subtask("Subtask-3",
                Status.NEW, 1));

        taskManager.setSubtaskStatus(2, Status.DONE);
        taskManager.setSubtaskStatus(3, Status.DONE);

        assertEquals(Status.DONE, taskManager.getEpic(1).getStatus(),
                "Epic status should be NEW");
    }
}