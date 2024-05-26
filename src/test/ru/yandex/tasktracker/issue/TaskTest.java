package ru.yandex.tasktracker.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    static Task task;

    @BeforeEach
    void setUp() {
        task = new Task("name", "description", Status.DONE);
    }

    @Test
    void testGetId() {
        assertEquals(0, task.getId());
    }

    @Test
    void testGetName() {
        assertEquals("name", task.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("description", task.getDescription());
    }

    @Test
    void testGetStatus() {

    }

    @Test
    void testSetId() {
        task.setId(1);
        assertEquals(1, task.getId());
    }

    @Test
    void testSetName() {
        task.setName("new name");
        assertEquals("new name", task.getName());
    }

    @Test
    void testSetDescription() {
        task.setDescription("new description");
        assertEquals("new description", task.getDescription());
    }

    @Test
    void testSetStatus() {
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }
}