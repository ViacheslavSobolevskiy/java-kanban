package ru.yandex.kanban;

import ru.yandex.kanban.issue.*;
import ru.yandex.kanban.service.TaskManagerImpl;

public class Main {
    public static void main(String[] args) {
        TaskManagerImpl taskManager = new TaskManagerImpl();

        /*
         * Нужно реализовать тестирование из тз:
         * - Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
         * - Распечатайте списки эпиков, задач и подзадач, через `System.out.println(..)`
         * - Измените статусы созданных объектов, распечатайте. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
         * - И, наконец, попробуйте удалить одну из задач и один из эпиков.
         */

        // Создайте 2 задачи,
        taskManager.createTask(new Task(1L, "Task-1", "Task-1", Status.NEW));
        taskManager.createTask(new Task(2L, "Task-2", "Task-2", Status.NEW));
        // один эпик с 2 подзадачам,
        taskManager.createEpic(new Epic(3L, "Epic-3", "Epic-3", Status.NEW));
        taskManager.createSubtask(new Subtask(4L, 3L ,"Subtask-4", "Subtask-4", Status.NEW));
        taskManager.createSubtask(new Subtask(5L, 3L, "Subtask-5", "Subtask-5", Status.NEW));
        // а другой эпик с 1 подзадачей.
        taskManager.createEpic(new Epic(6L, "Epic-6", "Epic-6", Status.NEW));
        taskManager.createSubtask(new Subtask(7L, 6L, "Subtask-7", "Subtask-7", Status.NEW));

        // Распечатайте списки эпиков,
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getAllEpics());
        // задач и подзадач,
        System.out.println("Списки задач:");
        System.out.println(taskManager.getAllTasks());
        // и подзадач через `System.out.println(..)`.
        System.out.println("Списки подзадач:");
        System.out.println(taskManager.getAllSubtasks());

        // Измените статусы созданных объектов
        taskManager.updateTaskStatusById(1L, Status.IN_PROGRESS);
        taskManager.updateTaskStatusById(2L, Status.DONE);
        taskManager.updateSubtaskStatusById(4L, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatusById(5L, Status.DONE);
        taskManager.updateSubtaskStatusById(7L, Status.DONE);
        // Распечатайте.
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Списки задач:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Списки подзадач:");
        System.out.println(taskManager.getAllSubtasks());
        // Проверьте, что статус задачи
        if (taskManager.getTaskById(1L).getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Неверно обновился статус задачи");
        }
        if (taskManager.getTaskById(2L).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус задачи");
        }
        // и подзадачи сохранился,
        if (taskManager.getSubtaskById(4L).getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Неверно обновился статус подзадачи");
        }
        if (taskManager.getSubtaskById(5L).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус подзадачи");
        }
        if (taskManager.getSubtaskById(7L).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус подзадачи");
        }
        // а статус эпика рассчитался по статусам подзадач.
        if (taskManager.getEpicById(3L).getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Неверно обновился статус эпика");
        }
        if (taskManager.getEpicById(6L).getStatus() != Status.DONE) {
            throw new IllegalStateException("Неверно обновился статус эпика");
        }

        // И, наконец, попробуйте удалить одну из задач
        taskManager.removeTaskById(2L);
        taskManager.removeSubtaskById(5L); // Вероятно, упущено
        // и один из эпиков.
        taskManager.removeEpicById(3L);

        // Смотрим, что осталось.
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Списки задач:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Списки подзадач:");
        System.out.println(taskManager.getAllSubtasks());

        /*
         * Ответ:
         * Проверено. Все отработало штатно
         */
    }
}
