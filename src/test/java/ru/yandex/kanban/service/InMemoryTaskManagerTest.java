package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.utility.Managers;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 6. Проверьте, что InMemoryTaskManager действительно добавляет задачи
 * разного типа и может найти их по id
 * <p>
 * 7. Проверьте, что задачи с заданным id и сгенерированным id не
 * конфликтуют внутри менеджера
 */

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }
    
    @Test
    void createAndFindTaskById() {
        Task task1 = new Task("Задача 1", "Задача 1", Status.IN_PROGRESS);
        int task1Id = taskManager.createTask(task1);

        Task task2 = taskManager.getTaskById(task1Id);

        assertNotNull(task2);
        assertEquals(task1, task2);
    }

    @Test
    void createAndFindEpicById() {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1");
        int epic1Id = taskManager.createEpic(epic1);

        Epic epic2 = taskManager.getEpicById(epic1Id);

        assertNotNull(epic2);
        assertEquals(epic1, epic2);
    }

    @Test
    void createAndFindSubtaskById() {
        Epic epic = new Epic("Эпик 1", null);
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", null, Status.DONE);
        int subtask1Id = taskManager.createSubtask(subtask1);

        Subtask subtask2 = taskManager.getSubtaskById(subtask1Id);

        assertNotNull(subtask2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void manualIdAndGeneratedIdNotConflict() {
        // Создаём задачу с id = 0
        Task task0 = new Task(0, "task0", "task", Status.NEW);
        // Добавляем через put в менеджер
        ((InMemoryTaskManager) taskManager).tasks.put(0, task0);

        // Проверяем, что задача с id = 0 существует
        assertEquals(task0, taskManager.getTaskById(0));


        // Создаём задачу через createTask
        Task _task0 = new Task("_task0", "_task0", Status.NEW);
        Integer _task0Id = taskManager.createTask(_task0);
        // Проверяем, что задача добавилась
        assertEquals(_task0, taskManager.getTaskById(_task0Id));

        // Проверяем, что новый id сгенерирован с учетом id = 0
        assertNotEquals(0, _task0Id);

    }

    @Test
    void taskNotChangedAfterAddingToManager() {
        Task original = new Task("TestName", "TestDesc", Status.IN_PROGRESS);
        // Копия для сравнения
        Task before = original.clone();

        int id = taskManager.createTask(original);
        Task fromManager = taskManager.getTaskById(id);

        assertEquals(before.getName(), fromManager.getName(), "Имя задачи изменилось");
        assertEquals(before.getDescription(), fromManager.getDescription(), "Описание задачи изменилось");
        assertEquals(before.getStatus(), fromManager.getStatus(), "Статус задачи изменился");
        assertNotNull(fromManager.getId(), "ID задачи не установлен");
    }

    @Test
    void epicNotChangeAfterAddingToManager() {
        Epic orig = new Epic("EpicName", "EpicDesc");
        Epic save_orig = orig.clone();

        int id = taskManager.createEpic(orig);
        Epic fromManager = taskManager.getEpicById(id);

        assertEquals(save_orig.getName(), fromManager.getName(), "Имя эпика изменилось");
        assertEquals(save_orig.getDescription(), fromManager.getDescription(), "Описание эпика изменилось");
        assertNotNull(fromManager.getId(), "ID эпика не установлен");
        // Для эпика можно проверить, что список подзадач пустой
        assertTrue(fromManager.getDependentSubtaskIds().isEmpty(), "Список подзадач эпика не пуст после добавления");
    }

    @Test
    void subtaskNotChangedAfterAddingToManager() {
        Epic epic = new Epic("Эпик", "Эпик");
        Integer epicId = taskManager.createEpic(epic);

        Subtask orig = new Subtask(epicId, "Subtask", "Subtask", Status.NEW);
        Subtask save_orig = orig.clone();

        int id = taskManager.createSubtask(orig);
        Subtask fromManager = taskManager.getSubtaskById(id);

        assertEquals(save_orig.getName(), fromManager.getName(), "Имя подзадачи изменилось");
        assertEquals(save_orig.getDescription(), fromManager.getDescription(), "Описание подзадачи изменилось");
        assertEquals(save_orig.getStatus(), fromManager.getStatus(), "Статус подзадачи изменился");
        assertEquals(save_orig.getEpicId(), fromManager.getEpicId(), "EpicId подзадачи изменился");
        assertNotNull(fromManager.getId(), "ID подзадачи не установлен");
    }
}
