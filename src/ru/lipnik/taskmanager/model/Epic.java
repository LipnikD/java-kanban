package ru.lipnik.taskmanager.model;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Objects;
import java.util.Comparator;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateStartTimeDuration() {
        Optional<LocalDateTime> minStartTime = subtasks.stream()
                .map(subtask -> subtask.startTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());
        Optional<LocalDateTime> maxEndTime = subtasks.stream()
                .filter(subtask -> subtask.duration != null)
                .map(subtask -> subtask.startTime.plus(subtask.duration))
                .max(Comparator.naturalOrder());
        if (minStartTime.isPresent()) {
            startTime = minStartTime.get();
            maxEndTime.ifPresent(localDateTime -> duration = Duration.between(startTime, localDateTime));
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        updateStartTimeDuration();
        return super.getEndTime();
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                ", subtasks=" + subtasks +
                "} ";
    }
}
