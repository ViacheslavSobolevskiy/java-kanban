package ru.yandex.tasktracker;

import ru.yandex.tasktracker.issue.*;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        HistoryManager historyManager = new HistoryManager();
        TaskManager taskManager = new TaskManager(historyManager);

        System.out.println("Создание Issues ..........................");
        taskManager.createTask(new Task("Task-1", "Task-1", Status.NEW));
        taskManager.createTask(new Task("Task-2", "Task-2", Status.NEW));
        taskManager.createEpic(new Epic("Epic-3", "Epic-3", Status.NEW));
        taskManager.createEpic(new Epic("Epic-4", "Epic-4", Status.NEW));

        taskManager.createSubTask(new SubTask("Subtask-5", "Subtask-5", Status.NEW, 3));
        taskManager.createSubTask(new SubTask("Subtask-6", "Subtask-6", Status.NEW, 3));
        taskManager.createSubTask(new SubTask("Subtask-7", "Subtask-7", Status.NEW, 4));
        taskManager.createSubTask(new SubTask("Subtask-8", "Subtask-8", Status.NEW, 4));

        System.out.println("Получение Issues по id ..........................");
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getEpic(4);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.getEpic(4);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.getSubTask(7);
        taskManager.getSubTask(8);
        taskManager.getSubTask(7);
        taskManager.getTask(2);

        System.out.println("Текущие Tasks ..........................");
        taskManager.printAllTasks();
        taskManager.printAllSubTasks();
        taskManager.printAllEpics();

        System.out.println("Добавляем задачи ..........................");
        System.out.println("--- Create task ---");
        taskManager.createTask(new Task("Описание-9", "Task-9", Status.NEW));
        taskManager.createTask(new Task("Описание-10", "Task-10", Status.NEW));
        taskManager.printAllTasks();

        System.out.println("Тест статуса Tasks ..........................");
        taskManager.setTaskStatus(1, Status.IN_PROGRESS);
        taskManager.printTask(1);

        taskManager.printHistory();

        System.out.println("Эпики ................");
        taskManager.printAllEpics();
        System.out.println("Get epic ................");
        taskManager.printEpic(3);
        System.out.println("Update epic ................");
        taskManager.setEpicStatus(3, Status.DONE);
        taskManager.printEpic(3);
        taskManager.printAllSubTasks();

        taskManager.printHistory();

        taskManager.printEpic(3);
        taskManager.setSubTaskStatus(5, Status.IN_PROGRESS);
        taskManager.printEpic(3);
    }
}
