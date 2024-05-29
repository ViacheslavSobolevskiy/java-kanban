package ru.yandex.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.issue.Epic;
import ru.yandex.tasktracker.issue.Status;
import ru.yandex.tasktracker.issue.Subtask;
import ru.yandex.tasktracker.issue.Task;
import ru.yandex.tasktracker.util.Managers;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    public static TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

    @BeforeEach
    void setUp() {
        ((InMemoryTaskManager) taskManager).reset();
    }

    @Test
    void testCreateTask() {
        Task task = new Task("Task-1", "Task-1", Status.NEW);
        int taskId = taskManager.createTask(task);
        assertNotEquals(0, taskId);
        assertEquals(task, taskManager.getTask(taskId));
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Epic-1", "Epic-1", Status.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask-2", "Subtask-2", Status.NEW, epicId);
        subtask.setEpicId(epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        assertNotEquals(0, subtaskId);
        assertEquals(subtask, taskManager.getSubtask(subtaskId));
        assertTrue(epic.getSubtaskIds().contains(subtaskId));
    }

    @Test
    void testUpdateTaskStatus() {
        Task task = new Task("Task-1", "Task-1", Status.NEW);
        int taskId = taskManager.createTask(task);
        taskManager.updateTaskStatus(taskId, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getTask(taskId).getStatus());
    }

    @Test
    void testUpdateSubtaskStatus() {
        Epic epic = new Epic("Epic-1", "Epic-1", Status.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask-2", "Subtask-2", Status.DONE, epicId);
        subtask.setEpicId(epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        taskManager.updateSubtaskStatus(subtaskId, Status.DONE);
        assertEquals(Status.DONE, taskManager.getSubtask(subtaskId).getStatus());
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testUpdateEpicStatus() {
        Epic epic = new Epic("Epic-1", "Epic-1", Status.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask-2", "Subtask-2", Status.DONE, epicId);
        subtask.setEpicId(epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        taskManager.updateSubtaskStatus(subtaskId, Status.DONE);
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Old Task", "Task-1", Status.NEW);
        taskManager.createTask(task);

        Task updatedTask = new Task("New Task", "Task-2", Status.DONE);
        taskManager.updateTask(1, updatedTask);

        assertEquals(updatedTask, taskManager.getTask(1));
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic("Epic-1", "Epic-1", Status.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask-1", Status.NEW, 1);
        int subtaskId = taskManager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Subtask-2", Status.IN_PROGRESS, 1);
        taskManager.updateSubtask(subtaskId, updatedSubtask);

        Subtask actualSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(updatedSubtask, actualSubtask);
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic("Epic-1", "Epic-1", Status.NEW);
        int epicId = taskManager.createEpic(epic);

        Epic newEpic = new Epic("New Epic", "New Description", Status.IN_PROGRESS);

        taskManager.updateEpic(epicId, newEpic);

        assertEquals(newEpic, taskManager.getEpic(epicId));
    }
}