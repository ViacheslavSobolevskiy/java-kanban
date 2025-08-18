package ru.yandex.kanban.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.kanban.issue.Task;

/**
 * Объявите класс InMemoryHistoryManager и перенесите в него часть кода для работы с
 * историей из класса InMemoryTaskManager. Новый класс InMemoryHistoryManager должен
 * реализовывать интерфейс HistoryManager.
 */

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        protected Task task;
        protected Node prev;
        protected Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        Integer id = task.getId();
        if (id == null)
            throw new IllegalArgumentException("Task id не должен быть null.");

        // Если задача уже есть в истории, удаляем её
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }

        // Добавляем задачу в конец списка
        linkLast(task.clone());
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null)
            removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node.prev != null)
            node.prev.next = node.next;
        else
            head = node.next;

        if (node.next != null)
            node.next.prev = node.prev;
        else
            tail = node.prev;

        nodeMap.remove(node.task.getId());
    }

    // Собирает все задачи из связного списка в ArrayList
    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(nodeMap.size());
        Node current = head;

        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }

        return tasks;
    }
}
