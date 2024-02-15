package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private static int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        if (task == null || tasks.containsValue(task)) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null || epics.containsValue(epic)) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        if (subtask == null || epic == null || subtasks.containsValue(subtask)) {
            return;
        }
        addEpicSubtask(epic, subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask : subtasks) {
                historyManager.add(subtask);
            }
            return subtasks;
        }
        return null;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks(Epic Epic) {
        if (Epic == null || Epic.getClass() != Epic.class) {
            return null;
        }
        return Epic.getSubtasks();
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || task.getClass() != Task.class || !tasks.containsValue(task)) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getClass() != Subtask.class || !subtasks.containsValue(subtask)) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || epic.getClass() != Epic.class || !epics.containsValue(epic)) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = getSubtaskEpic(id);
        if (epic != null) {
            deleteEpicSubtask(epic, subtask);
            subtasks.remove(id);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void setStatus(Object issue, Status status) {
        if (issue.getClass() == Task.class) {
            ((Task) issue).setStatus(status);
        } else if (issue.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) issue;
            subtask.setStatus(status);
            updateEpicStatus(Objects.requireNonNull(getSubtaskEpic(subtask.getId())));
        }
    }

    @Override
    public int newId() {
        return id++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
}