package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) {
        if (task == null || tasks.containsValue(task)) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        if (epic == null || epics.containsValue(epic)) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Epic epic, Subtask subtask) {
        if (subtask == null || epic == null || subtasks.containsValue(subtask)) {
            return;
        }
        addEpicSubtask(epic, subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks(Epic Epic) {
        if (Epic == null || Epic.getClass() != Epic.class) {
            return null;
        }
        return Epic.getSubtasks();
    }

    public void updateTask(Task task) {
        if (task == null || task.getClass() != Task.class || !tasks.containsValue(task)) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getClass() != Subtask.class || !subtasks.containsValue(subtask)) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpic(Epic epic) {
        if (epic == null || epic.getClass() != Epic.class || !epics.containsValue(epic)) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        deleteEpicSubtask(getSubtaskEpic(id), subtask);
        subtasks.remove(id);
    }

    public void setStatus(Object issue, Status status) {
        if (issue.getClass() == Task.class) {
            ((Task) issue).setStatus(status);
        } else if (issue.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) issue;
            subtask.setStatus(status);
            updateEpicStatus(getSubtaskEpic(subtask.getId()));
        }
    }

    public void updateEpicStatus(Epic epic) {

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allSubtasksNew = false;
            }
        }
        if (allSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void addEpicSubtask(Epic epic, Subtask subtask) {
        ArrayList<Subtask> subtasks = epic.getSubtasks();
        if (subtask == null) {
            return;
        }
        if (subtasks.contains(subtask)) {
            return;
        }
        subtasks.add(subtask);
        updateEpicStatus(epic);
    }

    public void deleteEpicSubtask(Epic epic, Subtask subtask) {
        if (subtask == null || subtask.getClass() != Subtask.class) {
            return;
        }
        epic.getSubtasks().remove(subtask);
    }

    private Epic getSubtaskEpic(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        for (int key : epics.keySet()) {
            Epic epic = epics.get(key);
            if (epic.getSubtasks().contains(subtask)) {
                return epic;
            }
        }
        return null;
    }

    public int newId() {
        return id++;
    }
}