package ru.yandex.kanban;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.kanban.issue.*;
import ru.yandex.kanban.service.TaskManagerImpl;

@Slf4j
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        TaskManagerImpl taskManager = new TaskManagerImpl();

        System.out.println("Пример работы с задачами ..........................");
        taskManager.updateTask(new Task("Task-1", "Task-1", Status.NEW));
        taskManager.updateTask(new Task("Task-2", "Task-2", Status.NEW));

        taskManager.updateEpic(new Epic("Epic-3", "Epic-3", Status.NEW));
        taskManager.updateEpic(new Epic("Epic-4", "Epic-4", Status.NEW));
        taskManager.updateEpic(new Epic("Epic-5", "Epic-5", Status.NEW));

        taskManager.updateSubtask(new Subtask(3L, "Subtask-6", "Subtask-6", Status.NEW));
        taskManager.updateSubtask(new Subtask(3L, "Subtask-7", "Subtask-7", Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(3L, "Subtask-8", "Subtask-8", Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(3L, "Subtask-9", "Subtask-9", Status.DONE));

        taskManager.updateSubtask(new Subtask(4L, "Subtask-10", "Subtask-10", Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(4L, "Subtask-11", "Subtask-11", Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(4L, "Subtask-12", "Subtask-12", Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(4L, "Subtask-13", "Subtask-13", Status.NEW));

        taskManager.updateSubtaskStatusById(6L, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatusById(7L, Status.DONE);
        taskManager.updateSubtaskStatusById(8L, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatusById(8L, Status.IN_PROGRESS);

        taskManager.getTaskById(1L);
        taskManager.getTaskById(2L);

        taskManager.getEpicById(3L);
        taskManager.getEpicById(4L);
        taskManager.getEpicById(5L);

        taskManager.updateTask(new Task("Описание-14", "Task-14", Status.DONE));
        taskManager.updateTask(new Task("Описание-15", "Task-15", Status.IN_PROGRESS));
        taskManager.removeSubtaskById(11L);
        taskManager.updateTaskStatusById(1L, Status.IN_PROGRESS);

        taskManager.updateSubtask(new Subtask(3L, "Subtask-14", "Subtask-14", Status.DONE));
        taskManager.getEpicById(3L);

        // Проверка авто-завершения эпика -
        // Эпик 4 должен быть DONE
        taskManager.updateSubtask(new Subtask(10L,4L, "Subtask-10", "Subtask-10", Status.DONE));
        taskManager.updateSubtask(new Subtask(12L, 4L, "Subtask-12", "Subtask-12", Status.DONE));
        taskManager.updateSubtask(new Subtask(13L,4L, "Subtask-13", "Subtask-13", Status.DONE));
        if (taskManager.getEpicById(4L).getStatus() != Status.DONE) {
            logger.error("Эпик 4 не завершен, а должен быть DONE");
        }

        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }
}
