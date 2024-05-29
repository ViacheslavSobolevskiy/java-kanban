package ru.yandex.tasktracker;

import ru.yandex.tasktracker.issue.Epic;
import ru.yandex.tasktracker.issue.Status;
import ru.yandex.tasktracker.issue.Subtask;
import ru.yandex.tasktracker.issue.Task;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.TaskManager;
import ru.yandex.tasktracker.util.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

        System.out.println("Пример работы с задачами ..........................");
        taskManager.createTask(new Task("Task-1", "Task-1", Status.NEW));
        taskManager.createTask(new Task("Task-2", "Task-2", Status.NEW));

        taskManager.createEpic(new Epic("Epic-3", "Epic-3", Status.NEW));
        taskManager.createEpic(new Epic("Epic-4", "Epic-4", Status.NEW));

        taskManager.createSubtask(new Subtask("Subtask-5", "Subtask-5", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Subtask-6", "Subtask-6", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Subtask-7", "Subtask-7", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Subtask-8", "Subtask-8", Status.NEW, 3));

        taskManager.createSubtask(new Subtask("Subtask-9", "Subtask-9", Status.NEW, 4));
        taskManager.createSubtask(new Subtask("Subtask-10", "Subtask-10", Status.NEW, 4));
        taskManager.createSubtask(new Subtask("Subtask-11", "Subtask-11", Status.NEW, 4));
        taskManager.createSubtask(new Subtask("Subtask-12", "Subtask-12", Status.NEW, 4));

        taskManager.updateSubtaskStatus(5, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(6, Status.DONE);
        taskManager.updateSubtaskStatus(7, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(8, Status.IN_PROGRESS);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);
        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);
        taskManager.getSubtask(7);
        taskManager.getSubtask(8);
        taskManager.getSubtask(7);
        taskManager.getTask(2);

        taskManager.createTask(new Task("Описание-9", "Task-9", Status.NEW));
        taskManager.createTask(new Task("Описание-10", "Task-10", Status.NEW));

        taskManager.updateTaskStatus(1, Status.IN_PROGRESS);

        Subtask updatedSubtask = new Subtask("Subtask-2", Status.IN_PROGRESS, 1);
        taskManager.updateSubtask(6, updatedSubtask);

        Epic updatedEpic = new Epic("Epic-3", "Epic-3", Status.DONE);
        taskManager.updateEpic(3, updatedEpic);

        System.out.println(taskManager.getHistory());

    }
}
