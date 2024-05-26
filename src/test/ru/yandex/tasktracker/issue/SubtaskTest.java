package ru.yandex.tasktracker.issue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void getEpicId() {
        Subtask subtask = new Subtask(1);
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void setEpicId() {
        Subtask subtask = new Subtask(1);
        subtask.setEpicId(2);
        assertEquals(2, subtask.getEpicId());
    }
}