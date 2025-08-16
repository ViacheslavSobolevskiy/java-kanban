package ru.yandex.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.issue.Epic;
import ru.yandex.kanban.issue.Status;
import ru.yandex.kanban.issue.Subtask;
import ru.yandex.kanban.issue.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;

    private File newTempFile() throws IOException {
        tempFile = File.createTempFile("tmp-", ".csv");
        Files.writeString(tempFile.toPath(), "id,type,name,status,description,epic\n", StandardCharsets.UTF_8);
        tempFile.deleteOnExit();
        return tempFile;
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            assertTrue(tempFile.delete(), "Не получилось удалить временный файл");
        }
    }

    @Test
    void saveAndLoad() throws Exception {
        File file = newTempFile();

        try (FileBackedTaskManager manager = new FileBackedTaskManager(file)) {
            int taskId = manager.createTask(new Task("Task0", "Desc0", Status.NEW));
            int epicId = manager.createEpic(new Epic("Epic1", "Desc1"));
            int subId = manager.createSubtask(new Subtask(epicId, "Sub3", "Desc3", Status.DONE));
            assertNotNull(manager.getTaskById(taskId));
            assertNotNull(manager.getEpicById(epicId));
            assertNotNull(manager.getSubtaskById(subId));
        }

        try (FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file)) {
            assertEquals(1, loaded.getAllTasks().size());
            assertEquals(1, loaded.getAllEpics().size());
            assertEquals(1, loaded.getAllSubtasks().size());

            Epic loadedEpic = loaded.getAllEpics().getFirst();
            List<Integer> subIds = new ArrayList<>(loadedEpic.getDependentSubtaskIds());
            assertEquals(1, subIds.size(), "Эпик должен иметь одну подзадачу");
        }
    }
}
