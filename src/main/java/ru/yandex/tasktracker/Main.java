package ru.yandex.tasktracker;

import ru.yandex.tasktracker.issue.*;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        TaskManager inMemoryTaskManager = new InMemoryTaskManager(inMemoryHistoryManager);

        System.out.println("Создание Issues ..........................");
        inMemoryTaskManager.createTask(new Task("Task-1", "Task-1", Status.NEW));
        inMemoryTaskManager.createTask(new Task("Task-2", "Task-2", Status.NEW));

        inMemoryTaskManager.createSubtask(new Subtask("Subtask-5", "Subtask-5", Status.NEW, 3));
        inMemoryTaskManager.createSubtask(new Subtask("Subtask-6", "Subtask-6", Status.NEW, 3));
        inMemoryTaskManager.createSubtask(new Subtask("Subtask-7", "Subtask-7", Status.NEW, 4));
        inMemoryTaskManager.createSubtask(new Subtask("Subtask-8", "Subtask-8", Status.NEW, 4));

        System.out.println("Получение Issues по id ..........................");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getEpic(4);
        inMemoryTaskManager.getSubtask(5);
        inMemoryTaskManager.getSubtask(6);
        inMemoryTaskManager.getEpic(4);
        inMemoryTaskManager.getSubtask(5);
        inMemoryTaskManager.getSubtask(6);
        inMemoryTaskManager.getSubtask(7);
        inMemoryTaskManager.getSubtask(8);
        inMemoryTaskManager.getSubtask(7);
        inMemoryTaskManager.getTask(2);

        System.out.println("Текущие Tasks ..........................");
        inMemoryTaskManager.printAllTasks();
        inMemoryTaskManager.printAllSubtasks();
        inMemoryTaskManager.printAllEpics();

        System.out.println("Добавляем задачи ..........................");
        System.out.println("--- Create task ---");
        inMemoryTaskManager.createTask(new Task("Описание-9", "Task-9", Status.NEW));
        inMemoryTaskManager.createTask(new Task("Описание-10", "Task-10", Status.NEW));
        inMemoryTaskManager.printAllTasks();

        System.out.println("Тест статуса Tasks ..........................");
        inMemoryTaskManager.setTaskStatus(1, Status.IN_PROGRESS);
        inMemoryTaskManager.printTask(1);

        inMemoryTaskManager.printHistory();

        System.out.println("Эпики ................");
        inMemoryTaskManager.printAllEpics();
        System.out.println("Get epic ................");
        inMemoryTaskManager.printEpic(3);
        System.out.println("Update epic ................");
        inMemoryTaskManager.setEpicStatus(3, Status.DONE);
        inMemoryTaskManager.printEpic(3);
        inMemoryTaskManager.printAllSubtasks();

        inMemoryTaskManager.printHistory();

    }
}
