package ru.lipnik.taskmanager.model;

import java.util.ArrayList;

public class Epic extends Task {
    public final ArrayList<Subtask> subtasks;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epyc{" +
                super.toString() +
                ", subtasks=" + subtasks +
                "} ";
    }
}
