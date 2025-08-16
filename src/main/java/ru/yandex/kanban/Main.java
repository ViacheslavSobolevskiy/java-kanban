package ru.yandex.kanban;

import ru.yandex.kanban.exception.ManagerSaveException;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.service.FileBackedTaskManager;
import ru.yandex.kanban.service.TaskManager;
import ru.yandex.kanban.utility.Managers;

import java.io.File;

public class Main {
    private static final String FILE_NAME = "tasks.csv";

    public static void main(String[] args) {
        checkSprint7();
    }

    private static void printHistory(TaskManager manager, String title) {
        System.out.println("\n--- " + title + " ---");
        System.out.println("История просмотров:");
        var history = manager.getHistory();
        if (history.isEmpty())
            System.out.println("  История пуста");
        else {
            for (int i = 0; i < history.size(); i++) {
                Task task = history.get(i);
                System.out.println("  " + (i + 1) + ". " + task.getClass().getSimpleName() +
                        " ID=" + task.getId() + " \"" + task.getName() + "\"");
            }
        }
        System.out.println("Всего в истории: " + history.size() + " элементов\n");
    }

    private static void checkSprint6() {
        System.out.println("=== Sprint 6 ===");
        System.out.println("=== Пользовательский сценарий (необязательно) ===\n");

        TaskManager taskManager = Managers.getDefault();

        // 1. Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач
        System.out.println("1. Создание задач:");

        Task task1 = new Task("Задача 1", "Первая задача", Status.NEW);
        Task task2 = new Task("Задача 2", "Вторая задача", Status.IN_PROGRESS);
        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);

        Epic epicWithSubtasks = new Epic("Эпик с подзадачами", "Эпик с тремя подзадачами");
        int epicWithSubtasksId = taskManager.createEpic(epicWithSubtasks);

        Subtask subtask1 = new Subtask(epicWithSubtasksId, "Подзадача 1", "Первая подзадача",
                Status.NEW);
        Subtask subtask2 = new Subtask(epicWithSubtasksId, "Подзадача 2", "Вторая подзадача",
                Status.IN_PROGRESS);
        Subtask subtask3 = new Subtask(epicWithSubtasksId, "Подзадача 3", "Третья подзадача",
                Status.DONE);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);
        int subtask3Id = taskManager.createSubtask(subtask3);

        Epic epicEmpty = new Epic("Пустой эпик", "Эпик без подзадач");
        int epicEmptyId = taskManager.createEpic(epicEmpty);

        System.out.println("Созданы: 2 задачи, 1 эпик с 3 подзадачами, 1 пустой эпик\n");

        // 2. Запросите созданные задачи несколько раз в разном порядке
        System.out.println("2. Запрос задач в разном порядке:");

        System.out.println("Первый раунд запросов:");
        taskManager.getTaskById(task1Id);
        taskManager.getEpicById(epicWithSubtasksId);
        taskManager.getSubtaskById(subtask2Id);
        taskManager.getTaskById(task2Id);
        printHistory(taskManager, "После первого раунда");

        System.out.println("Второй раунд запросов:");
        taskManager.getSubtaskById(subtask1Id);
        taskManager.getTaskById(task1Id); // Повторный запрос
        taskManager.getSubtaskById(subtask3Id);
        taskManager.getEpicById(epicEmptyId);
        printHistory(taskManager, "После второго раунда");

        System.out.println("Третий раунд запросов:");
        taskManager.getEpicById(epicWithSubtasksId); // Повторный запрос
        taskManager.getSubtaskById(subtask2Id); // Повторный запрос
        taskManager.getTaskById(task2Id); // Повторный запрос
        printHistory(taskManager, "После третьего раунда");

        // 3. Удалите задачу, которая есть в истории
        System.out.println("3. Удаление задачи из истории:");
        System.out.println("Удаляем задачу с ID: " + task1Id);
        taskManager.removeTaskById(task1Id);
        printHistory(taskManager, "После удаления задачи");

        // 4. Удалите эпик с тремя подзадачами
        System.out.println("4. Удаление эпика с подзадачами:");
        System.out.println("Удаляем эпик с ID: " + epicWithSubtasksId + " и все его подзадачи");
        taskManager.removeEpicById(epicWithSubtasksId);
        printHistory(taskManager, "После удаления эпика с подзадачами");

        System.out.println("=== Пользовательский сценарий завершен ===");
    }

    private static void checkSprint7() {
        int taskId;
        int epicId;
        int subtaskId;

        System.out.println("=== Sprint 7 ===");
        System.out.println("=== FileBackedTaskManager ===");

        File file = new File(FILE_NAME);
        if (file.exists()) {
            boolean result = file.delete();
            if (!result) {
                System.out.println("Файл не удален");
            }
        }

        try (FileBackedTaskManager manager1 = new FileBackedTaskManager(file)) {
            // Заводим issues.
            taskId = manager1.createTask(new Task("Task0", "Description task0", Status.NEW));
            epicId = manager1.createEpic(new Epic("Epic1", "Description epic1"));
            subtaskId = manager1.createSubtask(new Subtask(epicId, "Sub Task2", "Description sub task2", Status.DONE));
            // Обновляем статус эпика
            Epic epic = manager1.getEpicById(epicId);
            epic.setStatus(Status.DONE);
            manager1.updateEpic(epic);
            System.out.println("Первый менеджер:");
            System.out.println("Задач: " + manager1.getAllTasks().size());
            System.out.println("Эпиков: " + manager1.getAllEpics().size());
            System.out.println("Подзадач: " + manager1.getAllSubtasks().size());
        } catch (
                Exception e) {
            throw new ManagerSaveException("Ошибка сохранения в файл" + e.getMessage());
        }

        try (FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file)) {
            System.out.println("\nВторой менеджер (загружен из файла):");
            System.out.println("Задач: " + manager2.getAllTasks().size());
            System.out.println("Эпиков: " + manager2.getAllEpics().size());
            System.out.println("Подзадач: " + manager2.getAllSubtasks().size());
            // Проверяем, что все задачи есть в новом менеджере
            boolean allTasksPresent = manager2.getTaskById(taskId) != null &&
                    manager2.getEpicById(epicId) != null &&
                    manager2.getSubtaskById(subtaskId) != null;
            System.out.println("Issues восстановлены: " + (allTasksPresent ? "корректно" : "некорректно"));
        } catch (
                Exception e) {
            System.out.println("Ошибка загрузки из файла: " + e.getMessage());
        }
    }
}
