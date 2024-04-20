package ru.lipnik.taskmanager.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.service.Managers;
import ru.lipnik.taskmanager.service.TaskManager;

import java.time.LocalDateTime;
import java.util.List;

class TaskTest {

    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {

        Task task = new Task(taskManager.newId(), "Test addNewTask", "Test addNewTask description");
        task.setStartTime(LocalDateTime.now().plusDays(4));
        task.setDurationOfMinutes(60);
        taskManager.addTask(task);
        Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(task.getId(), "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotNull(task.getEndTime(), "Ошибка планирования времени.");

        Task sameTimePlannedTask = new Task(taskManager.newId(), "Test", "Test description");
        sameTimePlannedTask.setStartTime(LocalDateTime.now().plusDays(4));
        sameTimePlannedTask.setDurationOfMinutes(1);
        taskManager.addTask(sameTimePlannedTask);
        assertNull(taskManager.getTask(sameTimePlannedTask.getId()), "Ошибка контроля пересечения интервалов задач.");

        Task badPlannedTask = new Task(taskManager.newId(), "Test period intersection", "");
        badPlannedTask.setStartTime(LocalDateTime.now().plusDays(4).minusHours(2));
        badPlannedTask.setDurationOfMinutes(121);
        taskManager.addTask(badPlannedTask);

        assertNull(taskManager.getTask(badPlannedTask.getId()), "Ошибка контроля пересечения интервалов задач.");

        badPlannedTask = new Task(taskManager.newId(), "Test period intersection", "");
        badPlannedTask.setStartTime(LocalDateTime.now().plusDays(4).plusMinutes(59));
        badPlannedTask.setDurationOfMinutes(10);
        taskManager.addTask(badPlannedTask);

        assertNull(taskManager.getTask(badPlannedTask.getId()), "Ошибка контроля пересечения интервалов задач.");

        Task goodPlannedTask = new Task(taskManager.newId(), "Test period intersection", "");
        goodPlannedTask.setStartTime(LocalDateTime.now().plusDays(4).minusHours(2));
        goodPlannedTask.setDurationOfMinutes(119);
        taskManager.addTask(goodPlannedTask);

        assertNotNull(taskManager.getTask(goodPlannedTask.getId()), "Ошибка контроля пересечения интервалов задач.");

        Task goodPlannedTask2 = new Task(taskManager.newId(), "Test period intersection", "");
        goodPlannedTask2.setStartTime(LocalDateTime.now().plusDays(4).plusMinutes(61));
        goodPlannedTask2.setDurationOfMinutes(5);
        taskManager.addTask(goodPlannedTask2);

        assertNotNull(taskManager.getTask(goodPlannedTask2.getId()), "Ошибка контроля пересечения интервалов задач.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        Task anotherTask = new Task(taskManager.newId(), "Test addNewTask2", "Test addNewTask2 description");
        taskManager.addTask(anotherTask);
        Task anotherSavedTask = taskManager.getTask(anotherTask.getId());
        assertEquals(4, taskManager.getHistory().size(), "История возвращается неверно.");

        assertNotEquals("", anotherSavedTask.toString(), "Представление задачи не должно быть пустым.");

        assertNotEquals(task.hashCode(), anotherTask.hashCode(), "ХЭШ разных задач не может быть одинаковым.");
        taskManager.deleteTask(Integer.MAX_VALUE);
        assertNull(taskManager.getTask(Integer.MAX_VALUE),
                "Получение задачи по несуществующему идентификатору невозможно.");

    }

    @Test
    void taskStatusFlow() {

        Task theatre = new Task(taskManager.newId(), "Купить билеты в театр",
                "Выбрать из еще не полученных впечатлений новый сюжет и театр.");
        taskManager.addTask(theatre);
        Task dogTraining = new Task(taskManager.newId(),"Научить собаку находить сокровища",
                "Питомец, регулярно улучшающий финансовое благосостояние, хорошо дополнит трудовые доходы.");
        taskManager.addTask(dogTraining);

        taskManager.setStatus(theatre, Status.DONE);
        String newDescription = "Кажется, что будет очень интересно!";
        theatre.setDescription(newDescription);
        taskManager.updateTask(theatre);
        assertEquals(newDescription, theatre.getDescription(), "Ошибка обновления описания задачи.");

        String newName = "Потерять билеты в театр";
        theatre.setName(newName);
        assertEquals(newName, theatre.getName(), "Ошибка обновления наименования задачи.");

        taskManager.setStatus(dogTraining, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, dogTraining.getStatus(), "Ошибка обновления статуса задачи.");
        taskManager.setStatus(dogTraining, Status.DONE);
        assertEquals(Status.DONE, dogTraining.getStatus(), "Ошибка обновления статуса задачи.");
    }
}