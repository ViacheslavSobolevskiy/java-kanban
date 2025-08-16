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
        Task task01 = new Task("task01", "task01", Status.NEW);
        Integer task01Id = taskManager.createTask(task01);
        // Проверяем, что задача добавилась
        assertEquals(task01, taskManager.getTaskById(task01Id));

        // Проверяем, что новый id сгенерирован с учетом id = 0
        assertNotEquals(0, task01Id);

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
        Epic saveOrig = orig.clone();

        int id = taskManager.createEpic(orig);
        Epic fromManager = taskManager.getEpicById(id);

        assertEquals(saveOrig.getName(), fromManager.getName(), "Имя эпика изменилось");
        assertEquals(saveOrig.getDescription(), fromManager.getDescription(), "Описание эпика изменилось");
        assertNotNull(fromManager.getId(), "ID эпика не установлен");
        // Для эпика можно проверить, что список подзадач пустой
        assertTrue(fromManager.getDependentSubtaskIds().isEmpty(), "Список подзадач эпика не пуст после добавления");
    }

    @Test
    void subtaskNotChangedAfterAddingToManager() {
        Epic epic = new Epic("Эпик", "Эпик");
        Integer epicId = taskManager.createEpic(epic);

        Subtask orig = new Subtask(epicId, "Subtask", "Subtask", Status.NEW);
        Subtask saveOrig = orig.clone();

        int id = taskManager.createSubtask(orig);
        Subtask fromManager = taskManager.getSubtaskById(id);

        assertEquals(saveOrig.getName(), fromManager.getName(), "Имя подзадачи изменилось");
        assertEquals(saveOrig.getDescription(), fromManager.getDescription(), "Описание подзадачи изменилось");
        assertEquals(saveOrig.getStatus(), fromManager.getStatus(), "Статус подзадачи изменился");
        assertEquals(saveOrig.getEpicId(), fromManager.getEpicId(), "EpicId подзадачи изменился");
        assertNotNull(fromManager.getId(), "ID подзадачи не установлен");
    }

    @Test
    void removeEpic_removesAllSubtasks() {
        // Создаём эпик с подзадачами
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(epicId, "Подзадача 1", "Описание 1", Status.NEW);
        Subtask subtask2 = new Subtask(epicId, "Подзадача 2", "Описание 2", Status.IN_PROGRESS);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);

        // Проверяем, что подзадачи созданы
        assertEquals(2, taskManager.getAllSubtasks().size());
        assertEquals(2, taskManager.getAllSubtasksByEpicId(epicId).size());

        // Удаляем эпик
        taskManager.removeEpicById(epicId);

        // Проверяем, что все подзадачи удалены
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertThrows(IllegalArgumentException.class, () -> taskManager.getSubtaskById(subtask1Id));
        assertThrows(IllegalArgumentException.class, () -> taskManager.getSubtaskById(subtask2Id));
        assertNull(taskManager.getEpicById(epicId));
    }

    @Test
    void removeSubtask_updatesEpicSubtaskIds() {
        // Создаём эпик с подзадачами
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(epicId, "Подзадача 1", "Описание 1", Status.NEW);
        Subtask subtask2 = new Subtask(epicId, "Подзадача 2", "Описание 2", Status.IN_PROGRESS);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);

        // Проверяем, что у эпика есть обе подзадачи
        assertEquals(2, taskManager.getAllSubtasksByEpicId(epicId).size());

        // Удаляем одну подзадачу
        taskManager.removeSubtaskById(subtask1Id);

        // Проверяем, что у эпика осталась только одна подзадача
        assertEquals(1, taskManager.getAllSubtasksByEpicId(epicId).size());
        assertEquals(subtask2Id, taskManager.getAllSubtasksByEpicId(epicId).getFirst().getId());
    }

    @Test
    void historyRemovesTasksWhenDeleted() {
        // Создаём задачи
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);

        // Просматриваем задачи (добавляем в историю)
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);

        // Проверяем, что задачи в истории
        assertEquals(2, taskManager.getHistory().size());

        // Удаляем одну задачу
        taskManager.removeTaskById(task1Id);

        // Проверяем, что задача удалена из истории
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task2Id, taskManager.getHistory().getFirst().getId());
    }

    @Test
    void historyRemovesEpicAndSubtasksWhenEpicDeleted() {
        // Создаём эпик с подзадачами
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(epicId, "Подзадача 1", "Описание 1", Status.NEW);
        Subtask subtask2 = new Subtask(epicId, "Подзадача 2", "Описание 2", Status.IN_PROGRESS);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);

        // Просматриваем все задачи (добавляем в историю)
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtask1Id);
        taskManager.getSubtaskById(subtask2Id);

        // Проверяем, что все в истории
        assertEquals(3, taskManager.getHistory().size());

        // Удаляем эпик
        taskManager.removeEpicById(epicId);

        // Проверяем, что все удалены из истории
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void taskMutationAfterAddingToManager_doesNotAffectStoredTask() {
        // Создаём задачу
        Task original = new Task("Оригинальное имя", "Оригинальное описание", Status.NEW);
        int taskId = taskManager.createTask(original);

        // Изменяем оригинальную задачу после добавления
        original.setName("Изменённое имя");
        original.setDescription("Изменённое описание");
        original.setStatus(Status.DONE);

        // Получаем задачу из менеджера
        Task fromManager = taskManager.getTaskById(taskId);

        // Проверяем, что задача в менеджере не изменилась
        assertEquals("Оригинальное имя", fromManager.getName());
        assertEquals("Оригинальное описание", fromManager.getDescription());
        assertEquals(Status.NEW, fromManager.getStatus());
    }

    @Test
    void updateTask_changesStoredTask() {
        // Создаём задачу
        Task task = new Task("Старое имя", "Старое описание", Status.NEW);
        int taskId = taskManager.createTask(task);

        // Изменяем задачу
        Task updatedTask = new Task(taskId, "Новое имя", "Новое описание", Status.DONE);
        taskManager.updateTask(updatedTask);

        // Получаем задачу из менеджера
        Task fromManager = taskManager.getTaskById(taskId);

        // Проверяем, что задача изменилась
        assertEquals("Новое имя", fromManager.getName());
        assertEquals("Новое описание", fromManager.getDescription());
        assertEquals(Status.DONE, fromManager.getStatus());
    }

    @Test
    void historyPreservesTaskVersions() {
        // Создаём задачу
        Task task = new Task("Версия 1", "Описание 1", Status.NEW);
        int taskId = taskManager.createTask(task);

        // Просматриваем задачу (добавляем в историю)
        taskManager.getTaskById(taskId);

        // Изменяем задачу
        Task updatedTask = new Task(taskId, "Версия 2", "Описание 2", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        // Снова просматриваем задачу
        taskManager.getTaskById(taskId);

        // Проверяем историю - должна быть только последняя версия
        assertEquals(1, taskManager.getHistory().size());
        Task historyTask = taskManager.getHistory().getFirst();
        assertEquals("Версия 2", historyTask.getName());
        assertEquals("Описание 2", historyTask.getDescription());
        assertEquals(Status.IN_PROGRESS, historyTask.getStatus());
    }
}
