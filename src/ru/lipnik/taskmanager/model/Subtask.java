package ru.lipnik.taskmanager.model;

import java.time.LocalDateTime;

public class Subtask extends Task {

    public Subtask(int id, String name, String description) {
        super(id, name, description);
    }

    public Subtask(int id, String name, String description, LocalDateTime startTime, long durationOfMinutes) {
        super(id, name, description, startTime, durationOfMinutes);
    }
}
