package ru.yandex.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.utility.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private final TaskManager taskManager = Managers.getDefault();

    private Task task1;
    private Task task2;

    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;

    private int taskId1;
    private int taskId2;

    private int subtaskId1;
    private int subtaskId2;
    private int subtaskId3;

    private int epicId1;
    private int epicId2;

    @BeforeEach
    void setUP() {
        task1 = new Task(null, "Task-1", "Task-Desc-1", Status.NEW);
        task2 = new Task(null, "Task-2", "Task-Desc-2", Status.IN_PROGRESS);
        taskId1 = taskManager.createTask(task1);
        taskId2 = taskManager.createTask(task2);

        var epic1 = new Epic(null, "Epic-1", "Epic-Desc-1");
        var epic2 = new Epic(null, "Epic-2", "Epic-Desc-2");
        epicId1 = taskManager.createEpic(epic1);
        epicId2 = taskManager.createEpic(epic2);

        subtask1 = new Subtask(epicId1, null, "Subtask-1", "Subtask-Desc-1", Status.NEW);
        subtask2 = new Subtask(epicId1, null, "Subtask-2", "Subtask-Desc-2", Status.IN_PROGRESS);
        subtask3 = new Subtask(epicId2, null, "Subtask-3", "Subtask-Desc-3", Status.DONE);
        subtaskId1 = taskManager.createSubtask(subtask1);
        subtaskId2 = taskManager.createSubtask(subtask2);
        subtaskId3 = taskManager.createSubtask(subtask3);
    }

    @AfterEach
    void tearDown() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
    }

    @Test
    void getIssues() {
        Task takenTask1 = taskManager.getTaskById(taskId1);
        Task takenTask2 = taskManager.getTaskById(taskId2);

        assertNotNull(takenTask1);
        assertNotNull(takenTask2);

        assertEquals("Task-1", takenTask1.getName());
        assertEquals("Task-2", takenTask2.getName());

        assertEquals(Status.NEW, takenTask1.getStatus());
        assertEquals(Status.IN_PROGRESS, takenTask2.getStatus());

        Subtask takenSubtask1 = taskManager.getSubtaskById(subtaskId1);
        Subtask takenSubtask2 = taskManager.getSubtaskById(subtaskId2);
        Subtask takenSubtask3 = taskManager.getSubtaskById(subtaskId3);

        assertNotNull(takenSubtask1);
        assertNotNull(takenSubtask2);
        assertNotNull(takenSubtask3);

        assertEquals("Subtask-1", takenSubtask1.getName());
        assertEquals("Subtask-2", takenSubtask2.getName());
        assertEquals("Subtask-3", takenSubtask3.getName());

        assertEquals(Status.NEW, takenSubtask1.getStatus());
        assertEquals(Status.IN_PROGRESS, takenSubtask2.getStatus());
        assertEquals(Status.DONE, takenSubtask3.getStatus());

        Epic takenEpic1 = taskManager.getEpicById(epicId1);
        Epic takenEpic2 = taskManager.getEpicById(epicId2);

        assertNotNull(takenEpic1);
        assertNotNull(takenEpic2);

        assertEquals("Epic-1", takenEpic1.getName());
        assertEquals("Epic-2", takenEpic2.getName());

        assertEquals(Status.IN_PROGRESS, takenEpic1.getStatus());
        assertEquals(Status.DONE, takenEpic2.getStatus());
    }

    @Test
    void updateIssues() {
        Task updatedTask1 = new Task(taskId1, "Task Updated",
                "Desc Updated", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);
        Task fetched = taskManager.getTaskById(taskId1);

        assertNotNull(fetched);
        assertEquals("Task Updated", fetched.getName());
        assertEquals(Status.IN_PROGRESS, fetched.getStatus());

        Subtask updatedSubtask1 = new Subtask(epicId1, subtaskId1,
                "Subtask Updated", "Desc Updated", Status.DONE);
        taskManager.updateSubtask(updatedSubtask1);
        Subtask fetchedSubtask = taskManager.getSubtaskById(subtaskId1);

        assertNotNull(fetchedSubtask);
        assertEquals("Subtask Updated", fetchedSubtask.getName());
        assertEquals(Status.DONE, fetchedSubtask.getStatus());

        Epic updatedEpic1 = new Epic(epicId1, "Epic Updated", "Desc Updated");
        taskManager.updateEpic(updatedEpic1);
        Epic fetchedEpic = taskManager.getEpicById(epicId1);

        assertNotNull(fetchedEpic);
        assertEquals("Epic Updated", fetchedEpic.getName());
        assertEquals(Status.NEW, fetchedEpic.getStatus());
    }

    @Test
    void removeIssueById() {
        taskManager.removeTaskById(taskId1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.getTaskById(taskId1));

        taskManager.removeSubtaskById(subtaskId1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.getSubtaskById(subtaskId1));

        taskManager.removeEpicById(epicId1);
        assertNull(taskManager.getEpicById(epicId1));
    }

    @Test
    void getAllIssues() {
        List<Task> tasks = taskManager.getAllTasks();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(2, tasks.size());
        assertEquals(3, subtasks.size());
        assertEquals(2, epics.size());
    }

    @Test
    void removeAllIssues() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void getCorrectSubtasksNumberByEpicId() {
        assertEquals(2, taskManager.getAllSubtasksByEpicId(epicId1).size());
    }

    @Test
    void changeIssuesStatus() {
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(taskId1).getStatus());
        assertEquals(Status.DONE, taskManager.getTaskById(taskId2).getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        assertEquals(Status.DONE, taskManager.getSubtaskById(subtaskId1).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtaskId2).getStatus());

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId1).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId2).getStatus());
    }
}
