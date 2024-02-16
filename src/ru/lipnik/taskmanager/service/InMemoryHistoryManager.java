package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public <T extends Task> void add(T t) {
        history.add(t);
        if (history.size() == 11) {
            history.removeFirst();
        }
    }

    @Override
    public <T extends Task> void remove(T t) {
        history.remove(t);
    }
}
