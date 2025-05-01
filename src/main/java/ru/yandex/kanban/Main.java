package ru.yandex.kanban;

import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;
import ru.yandex.kanban.service.InMemoryTaskManager;
import ru.yandex.kanban.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        /*
         * Нужно реализовать тестирование из тз:
         * - Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
         * - Распечатайте списки эпиков, задач и подзадач, через `System.out.println(..)`
         * - Измените статусы созданных объектов, распечатайте. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
         * - И, наконец, попробуйте удалить одну из задач и один из эпиков.
         */

        // Создайте 2 задачи,
        Integer taskId0 = taskManager.createTask(new Task("Task-0", "Task-0", Status.NEW));
        Integer taskId1 = taskManager.createTask(new Task("Task-1", null, Status.NEW));

        // один эпик с 2 подзадачам,
        Integer epicId2 = taskManager.createEpic(new Epic("Epic-2", "Epic-2"));
        Integer subtaskId3 = taskManager.createSubtask(new Subtask(epicId2,
                "Subtask-3", "Subtask-3", Status.NEW));
        Integer subtaskId4 = taskManager.createSubtask(new Subtask(epicId2,
                "Subtask-4", "Subtask-4", Status.NEW));

        // а другой эпик с 1 подзадачей.
        Integer epicId5 = taskManager.createEpic(new Epic("Epic-5", "Epic-5"));
        Integer subtaskId6 = taskManager.createSubtask(new Subtask(epicId5,
                "Subtask-6", "Subtask-6", Status.NEW));

        // Распечатайте списки эпиков,
        // задач и подзадач,
        // и подзадач через `System.out.println(..)`.
        System.out.println("""
                Распечатайте списки эпиков,
                задач и подзадач,
                и подзадач через `System.out.println(..)`.
                """
        );
        printAllTasks(taskManager);

        // Измените статусы созданных объектов
        System.out.println("Измените статусы созданных объектов");
        taskManager.updateTask(new Task(taskId0, "Task-0", "Task-0" ,Status.IN_PROGRESS));
        taskManager.updateTask(new Task(taskId1, "Task-1", "Task-1" ,Status.DONE));
        taskManager.updateSubtask(new Subtask(epicId2, subtaskId3, "Subtask-3", "Subtask-3" ,Status.DONE));
        taskManager.updateSubtask(new Subtask(epicId2, subtaskId4, "Subtask-4", "Subtask-4" ,Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(epicId5, subtaskId6, "Subtask-6", "Subtask-6" ,Status.DONE));

        // Распечатайте.
        System.out.println("Распечатайте.");
        printAllTasks(taskManager);

        // Проверьте, что статус задачи
        System.out.println("Проверьте, что статус задачи");
        if (taskManager.getTaskById(taskId0).getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Неверно обновился статус задачи");
        }
        if (taskManager.getTaskById(taskId1).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус задачи");
        }
        // и подзадачи сохранился,
        System.out.println("и подзадачи сохранился,");
        if (taskManager.getSubtaskById(subtaskId3).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус подзадачи");
        }
        if (taskManager.getSubtaskById(subtaskId4).getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Неверно обновился статус подзадачи");
        }
        if (taskManager.getSubtaskById(subtaskId6).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус подзадачи");
        }
        // а статус эпика рассчитался по статусам подзадач.
        System.out.println("а статус эпика рассчитался по статусам подзадач.");
        if (taskManager.getEpicById(epicId2).getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Неверно обновился статус эпика");
        }
        if (taskManager.getEpicById(epicId5).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус эпика");
        }

        // И, наконец, попробуйте удалить одну из задач
        System.out.println("И, наконец, попробуйте удалить одну из задач");
        taskManager.removeTaskById(taskId0);
        // и один из эпиков.
        System.out.println("и один из эпиков.");
        taskManager.removeEpicById(epicId5);

        // Смотрим, что осталось.
        System.out.println("Смотрим, что осталось.");
        printAllTasks(taskManager);

        System.out.println("""
                Ответ:
                Проверено. Все отработало штатно
                """);

        System.out.println("Все вычистили");

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики и подзадачи:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
