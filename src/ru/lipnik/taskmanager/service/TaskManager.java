package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Status;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Epic epic, Subtask subtask);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    ArrayList<Subtask> getEpicSubtasks(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    int newId();

    void setStatus(Object issue, Status status);

    List<Task> getHistory();
}
