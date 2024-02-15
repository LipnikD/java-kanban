package ru.lipnik.taskmanager.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.service.Managers;
import ru.lipnik.taskmanager.service.TaskManager;

class SubtaskTest {

    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewSubtask() {

        Epic newSchool = new Epic(taskManager.newId(),"Перевести ребенка в новую школу",
                "Новая более сильная школа - хороший старт в развитии.");
        taskManager.addEpic(newSchool);
        Subtask learnMath = new Subtask(taskManager.newId(), "Подготовка к экзамену по математике",
                "Для успешной сдачи экзаменов точно понадобится уверенное знание математики.");
        taskManager.addSubtask(newSchool, learnMath);

        String newName = "New name";
        learnMath.setName(newName);
        taskManager.updateSubtask(learnMath);
        assertEquals(newName, learnMath.getName(), "Ошибка обновления наименования задачи.");

        assertEquals(learnMath, taskManager.getSubtask(learnMath.getId()), "Ошибка получения подзадачи.");
        assertEquals(1, taskManager.getSubtasks(newSchool).size(), "Неверное количество подзадач эпика.");
        taskManager.deleteSubtask(learnMath.getId());
        assertEquals(0, taskManager.getSubtasks(newSchool).size(), "Ошибка удаления подзадачи эпика.");
    }
}
