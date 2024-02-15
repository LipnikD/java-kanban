package ru.lipnik.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ManagersTest {

    static TaskManager taskManager;

    @Test
    void addNewTask() {
        taskManager = Managers.getDefault();
        for (int i = 0; i < 10; i++) {
            taskManager.newId();
        }
        assertEquals(11, taskManager.newId(), "Некорректно работает итератор идентификаторов.");
    }
}