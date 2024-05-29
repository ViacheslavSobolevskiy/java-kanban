package ru.yandex.tasktracker;

import ru.yandex.tasktracker.issue.*;
import ru.yandex.tasktracker.service.*;
import ru.yandex.tasktracker.service.TaskManager;
import ru.yandex.tasktracker.util.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        InMemoryTaskManager tm = (InMemoryTaskManager) taskManager;

        System.out.println("Создание Issues ..........................");
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

        System.out.println("Получение Issues по id ..........................");
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

        System.out.println("Текущие Tasks ..........................");
        tm.printAllTasks();
        tm.printAllSubtasks();
        tm.printAllEpics();

        System.out.println("Добавляем задачи ..........................");
        System.out.println("--- Create task ---");
        taskManager.createTask(new Task("Описание-9", "Task-9", Status.NEW));
        taskManager.createTask(new Task("Описание-10", "Task-10", Status.NEW));
        tm.printAllTasks();

        System.out.println("Тест статуса Tasks ..........................");
        tm.updateTaskStatus(1, Status.IN_PROGRESS);
        tm.printTask(1);

        tm.printHistory();

        System.out.println("Эпики ................");
        tm.printAllEpics();
        System.out.println("Get epic ................");
        tm.printEpic(3);
        System.out.println("Update epic ................");
        tm.updateEpicStatus(3, Status.DONE);
        tm.printEpic(3);
        tm.printAllSubtasks();

        tm.printHistory();

    }
}
