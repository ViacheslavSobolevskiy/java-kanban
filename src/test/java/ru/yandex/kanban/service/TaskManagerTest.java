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
    void GetIssues() {
        Task taken_task1 = taskManager.getTaskById(taskId1);
        Task taken_task2 = taskManager.getTaskById(taskId2);

        assertNotNull(taken_task1);
        assertNotNull(taken_task2);

        assertEquals("Task-1", taken_task1.getName());
        assertEquals("Task-2", taken_task2.getName());

        assertEquals(Status.NEW, taken_task1.getStatus());
        assertEquals(Status.IN_PROGRESS, taken_task2.getStatus());

        Subtask taken_subtask1 = taskManager.getSubtaskById(subtaskId1);
        Subtask taken_subtask2 = taskManager.getSubtaskById(subtaskId2);
        Subtask taken_subtask3 = taskManager.getSubtaskById(subtaskId3);

        assertNotNull(taken_subtask1);
        assertNotNull(taken_subtask2);
        assertNotNull(taken_subtask3);

        assertEquals("Subtask-1", taken_subtask1.getName());
        assertEquals("Subtask-2", taken_subtask2.getName());
        assertEquals("Subtask-3", taken_subtask3.getName());

        assertEquals(Status.NEW, taken_subtask1.getStatus());
        assertEquals(Status.IN_PROGRESS, taken_subtask2.getStatus());
        assertEquals(Status.DONE, taken_subtask3.getStatus());

        Epic taken_epic1 = taskManager.getEpicById(epicId1);
        Epic taken_epic2 = taskManager.getEpicById(epicId2);

        assertNotNull(taken_epic1);
        assertNotNull(taken_epic2);

        assertEquals("Epic-1", taken_epic1.getName());
        assertEquals("Epic-2", taken_epic2.getName());

        assertEquals(Status.IN_PROGRESS, taken_epic1.getStatus());
        assertEquals(Status.DONE, taken_epic2.getStatus());
    }

    @Test
    void updateIssues() {
        Task updated_task1 = new Task(taskId1, "Task Updated",
                "Desc Updated", Status.IN_PROGRESS);
        taskManager.updateTask(updated_task1);
        Task fetched = taskManager.getTaskById(taskId1);

        assertNotNull(fetched);
        assertEquals("Task Updated", fetched.getName());
        assertEquals(Status.IN_PROGRESS, fetched.getStatus());

        Subtask updated_subtask1 = new Subtask(epicId1, subtaskId1,
                "Subtask Updated", "Desc Updated", Status.DONE);
        taskManager.updateSubtask(updated_subtask1);
        Subtask fetched_subtask = taskManager.getSubtaskById(subtaskId1);

        assertNotNull(fetched_subtask);
        assertEquals("Subtask Updated", fetched_subtask.getName());
        assertEquals(Status.DONE, fetched_subtask.getStatus());

        Epic updated_epic1 = new Epic(epicId1, "Epic Updated", "Desc Updated");
        taskManager.updateEpic(updated_epic1);
        Epic fetched_epic = taskManager.getEpicById(epicId1);

        assertNotNull(fetched_epic);
        assertEquals("Epic Updated", fetched_epic.getName());
        assertEquals(Status.NEW, fetched_epic.getStatus());
    }

    @Test
    void removeIssueById() {
        taskManager.removeTaskById(taskId1);
        assertThrows(IllegalArgumentException.class, () -> this.taskManager.getTaskById(taskId1));

        taskManager.removeSubtaskById(subtaskId1);
        assertThrows(IllegalArgumentException.class, () -> this.taskManager.getSubtaskById(subtaskId1));

        taskManager.removeEpicById(epicId1);
        assertThrows(IllegalArgumentException.class, () -> this.taskManager.getEpicById(epicId1));
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
