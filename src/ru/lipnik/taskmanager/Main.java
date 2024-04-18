package ru.lipnik.taskmanager;

import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;
import ru.lipnik.taskmanager.service.FileBackedTaskManager;
import ru.lipnik.taskmanager.service.ManagerRestoreException;
import ru.lipnik.taskmanager.service.TaskManager;

import java.io.File;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager;
        try {
            taskManager = FileBackedTaskManager.loadFromFile(new File("./resources/Tasks.csv"));
        } catch (ManagerRestoreException exception) {
            taskManager = new FileBackedTaskManager();
        }

        System.out.println("Состояние истории после загрузки данных из файла:");
        System.out.println(taskManager.getHistory());

        Epic epic1 = new Epic(taskManager.newId(),"Эпик без подзадач",
                "Описание эпика без подзадач.");
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic(taskManager.newId(),"Эпик с подзадачами",
                "Описание эпика с подзадачами.");
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask(taskManager.newId(), "Первая подзадача",
                "");
        taskManager.addSubtask(epic2, subtask1);
        Subtask subtask2 = new Subtask(taskManager.newId(), "Вторая подзадача",
                "Описание второй подзадачи");
        taskManager.addSubtask(epic2, subtask2);

        Task task1 = new Task(taskManager.newId(), "Задача №1", "Описание первой задачи");
        task1.setStartTime(LocalDateTime.now().plusDays(10));
        task1.setDurationOfMinutes(240);
        taskManager.addTask(task1);

        Task task2 = new Task(taskManager.newId(), "Задача №2", "Описание второй задачи");
        task2.setStartTime(LocalDateTime.now().plusDays(9));
        task2.setDurationOfMinutes(2400);
        taskManager.addTask(task2);

        Task task3 = new Task(taskManager.newId(), "Задача №Три", "Описание 3 задачи");
        taskManager.addTask(task3);

        System.out.println("Перед обращением к задачам история должна быть пустой:");
        System.out.println(taskManager.getHistory());

        System.out.println("Обращение к задачам, эпикам и подзадачам");
        System.out.println(taskManager.getSubtask(subtask2.getId()));
        System.out.println(taskManager.getTask(task1.getId()));
        System.out.println(taskManager.getTask(task2.getId()));
        System.out.println(taskManager.getEpic(epic2.getId()));
        System.out.println(taskManager.getTask(task3.getId()));
        System.out.println(taskManager.getTask(task1.getId()));
        System.out.println(taskManager.getTask(task3.getId()));

        System.out.println("Состояние истории после обращения:");
        System.out.println(taskManager.getHistory());

        taskManager.deleteEpic(epic2.getId());

        System.out.println("Состояние истории после удаления эпика с подзадачами:");
        System.out.println(taskManager.getHistory());

        System.out.println("Отсортированный по приоритету список задач:");
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
