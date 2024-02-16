package ru.lipnik.taskmanager.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.service.Managers;
import ru.lipnik.taskmanager.service.TaskManager;

class EpicTest {

    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void complexEpicTest() {

        Epic summerDrive = new Epic(taskManager.newId(),"Подготовить машину к лету",
                "Комплекс мероприятий для комфортного вхождения в летний сезон активного вождения.");
        taskManager.addEpic(summerDrive);
        Subtask tyres = new Subtask(taskManager.newId(), "Купить летние шины",
                "Необходимо выбрать на Яндекс маркете лучшее предложение и приобрести новые шины");
        taskManager.addSubtask(summerDrive, tyres);
        Subtask inspection = new Subtask(taskManager.newId(), "Провести техосмотр",
                "Нужно провести регламентный осмотр всех узлов, агрегатов и тех. жидкостей");
        taskManager.addSubtask(summerDrive, inspection);
        Subtask sound = new Subtask(taskManager.newId(), "Модернизировать аудиосистему",
                "Установить акустику и необходимые компоненты (усилители, процессор) для дальних поездок");
        taskManager.addSubtask(summerDrive, sound);

        Epic savedEpic = taskManager.getEpic(summerDrive.getId());
        assertNotNull(savedEpic.getId(), "Эпик не найден.");
        assertEquals(3, taskManager.getEpicSubtasks(summerDrive.getId()).size(),
                "История возвращается неверно.");

        assertEquals(4, taskManager.getHistory().size(), "История возвращается неверно.");

        Epic newSchool = new Epic(taskManager.newId(),"Перевести ребенка в новую школу",
                "Новая более сильная школа - хороший старт в развитии.");
        taskManager.addEpic(newSchool);

        assertNotEquals(summerDrive, newSchool, "Разные эипки не должны быть равны.");

        String newName = "Просто эпик";
        newSchool.setName(newName);
        taskManager.updateEpic(newSchool);
        assertEquals(newName, newSchool.getName(), "Ошибка обновления наименования эпика.");

        taskManager.setStatus(tyres, Status.DONE);
        taskManager.setStatus(sound, Status.DONE);
        taskManager.deleteSubtask(inspection.getId());

        assertEquals(Status.DONE, summerDrive.getStatus(),
                "Ошибка обновления статуса эпика при выполнении подзадач.");
        assertNotEquals("", summerDrive.toString(), "Представление эпика не должно быть пустым.");

        int summerDriveId = summerDrive.getId();
        taskManager.deleteEpic(summerDriveId);
        assertNull(taskManager.getEpic(summerDriveId), "Ошибка удаления эпика.");
        assertEquals(1, taskManager.getEpics().size(), "Ошибка количества эпиков после удаления.");
        assertEquals(0, taskManager.getHistory().size(), "Ошибка очистки истории после удаления задач.");
    }
}