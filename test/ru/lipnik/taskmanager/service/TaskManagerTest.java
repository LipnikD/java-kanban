package ru.lipnik.taskmanager.service;

import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    void addTaskTest() {
        Task task = new Task(taskManager.newId(),"Task", "Description");
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Ошибка получения списка задач.");
        assertEquals(1, tasks.size(), "Некорректное количество задач в списке.");
        assertEquals(task, tasks.getFirst(), "Задачи в списке отличаются от ожидаемых.");
    }

    @Test
    void addEpicTest() {
        Epic epic = new Epic(taskManager.newId(), "Epic", "Epic description");
        taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Количество эпиков в списке неверное.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void addSubTaskTest() {
        Epic epic = new Epic(taskManager.newId(), "Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(taskManager.newId(), "Subtask", "Subtask description");
        subtask.setStartTime(LocalDateTime.now());
        taskManager.addSubtask(epic, subtask);

        final Subtask savedSubTask = taskManager.getSubtask(subtask.getId());

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubTask, "Подзадачи не совпадают.");

        final List<Subtask> subTasks = taskManager.getSubtasks(epic);

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Количество подзадач в списке неверное.");
        assertEquals(subtask, subTasks.getFirst(), "Подзадачи не совпадают.");
    }
}
