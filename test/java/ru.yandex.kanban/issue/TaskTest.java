package ru.yandex.kanban.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.service.TaskManager;
import ru.yandex.kanban.utility.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    /**
     * 1. проверьте, что экземпляры класса Task равны друг другу, если равен их id;
     */
    @Test
    void tasksWithEqualIdsAreEqual() {
        Task t1 = new Task(1, "task-1", "task-1", Status.NEW);
        Task t2 = new Task(1, "task-2", "task-2", Status.IN_PROGRESS);
        assertEquals(t1, t2);
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }
}
