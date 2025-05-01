package ru.yandex.kanban.issue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest {

    /**
     * 2. проверьте, что наследники класса Task равны друг другу, если равен их id;
     */
    @Test
    void epicsWithEqualIdsAreEqual() {
        Epic e1 = new Epic("Epic-1", "Epic-1");
        e1.setId(2);
        Epic e2 = new Epic("Epic-2", "Epic-2");
        e2.setId(2);
        assertEquals(e1, e2);
    }

    /**
     * 3. проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
     */
    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("Epic-1", "Epic-1");
        epic.setId(1);
        assertThrows(IllegalArgumentException.class, () -> epic.addSubtaskId(1));
    }
}
