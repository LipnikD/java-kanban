package ru.lipnik.taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {

    protected final int id;
    protected String name;
    protected String description;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(int code, String name, String description) {
        this.id = code;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int code, String name, String description, LocalDateTime startTime, long durationOfMinutes) {
        this.id = code;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationOfMinutes);
    }

    public Integer getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        if (this.status == Status.DONE) {
            return;
        }
        this.status = status;
    }

    public void setDurationOfMinutes(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public long getDurationToMinutes() {
        if (duration == null) {
            return 0;
        } else {
            return duration.toMinutes();
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return Optional.of(startTime.plus(duration)).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + getDurationToMinutes() +
                '}';
    }
}
