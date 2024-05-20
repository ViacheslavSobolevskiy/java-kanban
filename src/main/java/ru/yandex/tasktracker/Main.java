package ru.yandex.tasktracker;

import ru.yandex.tasktracker.issue.*;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        HistoryManager historyManager = new HistoryManager();
        TaskManager taskManager = new TaskManager(historyManager);

        System.out.println("Создание Issues ..........................");
        taskManager.createIssue(new Task("Task-1", "Task-1", Status.NEW));
        taskManager.createIssue(new Task("Task-2", "Task-2", Status.NEW));
        taskManager.createIssue(new Epic("Epic-3", "Epic-3", Status.NEW));
        taskManager.createIssue(new Epic("Epic-4", "Epic-4", Status.NEW));

        taskManager.createIssue(new SubTask("Subtask-5", "Subtask-5", Status.NEW, 3));
        taskManager.createIssue(new SubTask("Subtask-6", "Subtask-6", Status.NEW, 3));
        taskManager.createIssue(new SubTask("Subtask-7", "Subtask-7", Status.NEW, 4));
        taskManager.createIssue(new SubTask("Subtask-8", "Subtask-8", Status.NEW, 4));

        System.out.println("Получение Issues по id ..........................");
        taskManager.getIssueById(1);
        taskManager.getIssueById(3);
        taskManager.getIssueById(2);
        taskManager.getIssueById(5);
        taskManager.getIssueById(6);
        taskManager.getIssueById(4);
        taskManager.getIssueById(5);
        taskManager.getIssueById(6);
        taskManager.getIssueById(7);
        taskManager.getIssueById(8);
        taskManager.getIssueById(7);
        taskManager.getIssueById(2);

        System.out.println("Истории обращений к Issue ..........................");
        historyManager.printHistory();
        System.out.println("Текущие Tasks ..........................");
        taskManager.printAllIssuesByType(IssueType.TASK);
        taskManager.printAllIssuesByType(IssueType.SUBTASK);
        taskManager.printAllIssuesByType(IssueType.EPIC);

        System.out.println("Добавляем задачи ..........................");
        System.out.println("--- Create task ---");
        taskManager.createIssue(new Task("Описание-1", "Task-1", Status.NEW));
        taskManager.createIssue(new Task("Описание-2", "Task-2", Status.NEW));
        taskManager.printAllIssuesByType(IssueType.TASK);

        System.out.println("Тест статуса Tasks ..........................");
        taskManager.setIssueStatus(1, IssueType.TASK, Status.IN_PROGRESS);
        taskManager.printIssue(1, IssueType.TASK);

        System.out.println("Эпики ................");
        taskManager.printAllIssuesByType(IssueType.EPIC);
        System.out.println("Get epic ................");
        Epic epic = (Epic) taskManager.getIssueById(3);
        taskManager.printIssue(3, IssueType.EPIC);
        System.out.println("Update epic ................");
        taskManager.setIssueStatus(3, IssueType.EPIC, Status.DONE);
        taskManager.printIssue(3, IssueType.EPIC);
        taskManager.printAllIssuesByType(IssueType.SUBTASK);
    }
}
