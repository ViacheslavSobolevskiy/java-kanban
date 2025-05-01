package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void start() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void createAndGetTask() {
        Task task = new Task(null, "Test Task", null, Status.NEW);
        Integer id = manager.createTask(task);
        assertNotNull(id);

        Task taken_task = manager.getTaskById(id);
        assertEquals("Test Task", taken_task.getName());
        assertEquals(Status.NEW, taken_task.getStatus());
    }

    @Test
    void updateTask() {
        Task task = new Task(null, "Task", "Desc", Status.NEW);
        Integer id = manager.createTask(task);
        Task updated = new Task(id, "Task Updated", "Desc Updated", Status.IN_PROGRESS);
        manager.updateTask(updated);
        Task fetched = manager.getTaskById(id);
        assertEquals("Task Updated", fetched.getName());
        assertEquals(Status.IN_PROGRESS, fetched.getStatus());
    }

    @Test
    void removeTaskById() {
        Task task = new Task(null, "Task", "Desc", Status.NEW);
        Integer id = manager.createTask(task);
        manager.removeTaskById(id);
        assertThrows(IllegalArgumentException.class, () -> manager.getTaskById(id));
    }

    @Test
    void getAllTasks() {
        manager.createTask(new Task(null, "Task1", "Desc1", Status.NEW));
        manager.createTask(new Task(null, "Task2", "Desc2", Status.NEW));
        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void removeAllTasks() {
        manager.createTask(new Task(null, "Task1", "Desc1", Status.NEW));
        manager.createTask(new Task(null, "Task2", "Desc2", Status.NEW));
        manager.removeAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    // Epic tests
    @Test
    void createAndGetEpic() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer id = manager.createEpic(epic);
        assertNotNull(id);
        Epic fetched = manager.getEpicById(id);
        assertEquals("Epic1", fetched.getName());
        assertEquals(Status.NEW, fetched.getStatus());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer id = manager.createEpic(epic);
        Epic updated = new Epic(id, "Epic Updated", "Epic desc updated");
        manager.updateEpic(updated);
        Epic fetched = manager.getEpicById(id);
        assertEquals("Epic Updated", fetched.getName());
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer id = manager.createEpic(epic);
        manager.removeEpicById(id);
        assertThrows(IllegalArgumentException.class, () -> manager.getEpicById(id));
    }

    @Test
    void getAllEpics() {
        manager.createEpic(new Epic("Epic1", "Desc1"));
        manager.createEpic(new Epic("Epic2", "Desc2"));
        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size());
    }

    @Test
    void removeAllEpics() {
        manager.createEpic(new Epic("Epic1", "Desc1"));
        manager.createEpic(new Epic("Epic2", "Desc2"));
        manager.removeAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    // Subtask tests
    @Test
    void createAndGetSubtask() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask(epicId, "Subtask1", "Subtask desc", Status.NEW);
        Integer subtaskId = manager.createSubtask(subtask);
        assertNotNull(subtaskId);
        Subtask fetched = manager.getSubtaskById(subtaskId);
        assertEquals("Subtask1", fetched.getName());
        assertEquals(epicId, fetched.getEpicId());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask(epicId, "Subtask1", "Subtask desc", Status.NEW);
        Integer subtaskId = manager.createSubtask(subtask);
        Subtask updated = new Subtask(epicId, subtaskId, "Subtask Updated", "Desc Updated", Status.DONE);
        manager.updateSubtask(updated);
        Subtask fetched = manager.getSubtaskById(subtaskId);
        assertEquals("Subtask Updated", fetched.getName());
        assertEquals(Status.DONE, fetched.getStatus());
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask(epicId, "Subtask1", "Subtask desc", Status.NEW);
        Integer subtaskId = manager.createSubtask(subtask);
        manager.removeSubtaskById(subtaskId);
        assertThrows(IllegalArgumentException.class, () -> manager.getSubtaskById(subtaskId));
    }

    @Test
    void getAllSubtasks() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(epicId, "Sub1", "Desc1", Status.NEW));
        manager.createSubtask(new Subtask(epicId, "Sub2", "Desc2", Status.NEW));
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size());
    }

    @Test
    void removeAllSubtasks() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(epicId, "Sub1", "Desc1", Status.NEW));
        manager.createSubtask(new Subtask(epicId, "Sub2", "Desc2", Status.NEW));
        manager.removeAllSubtasks();
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void getAllSubtasksByEpicId() {
        Epic epic = new Epic("Epic1", "Epic description");
        Integer epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(epicId, "Sub1", "Desc1", Status.NEW));
        manager.createSubtask(new Subtask(epicId, "Sub2", "Desc2", Status.NEW));
        List<Subtask> subtasks = manager.getAllSubtasksByEpicId(epicId);
        assertEquals(2, subtasks.size());
    }
}