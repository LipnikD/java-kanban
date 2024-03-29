package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Task;

import java.util.List;

public interface HistoryManager {

    <T extends Task> void add(T t);
    <T extends Task> void remove(T t);
    List<Task> getHistory();

}
