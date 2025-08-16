package ru.yandex.kanban.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    /**
     * 5. убедитесь, что утилитарный класс всегда
     * возвращает проинициализированные и готовые к работе экземпляры менеджеров;
     */
    @Test
    void managersReturnInitializedManagers() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }
}
