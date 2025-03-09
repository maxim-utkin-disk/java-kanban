package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

public class Task implements Comparable<Task> {

    private String name;
    private String description;
    private int id;
    protected TaskState status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, TaskState status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        if (status == null) {
            this.status = TaskState.NEW;
        } else {
            this.status = status;
        }
        this.id = -1;
        this.startTime = startTime;
        if (duration == null) {
            this.duration = Duration.ofMinutes(0L);
        } else {
            this.duration = duration;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskState getStatus() {
        return status;
    }

    public void setStatus(TaskState status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // в ТЗ просят сравнивать только id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    // в ТЗ просят сравнивать только id
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Задача (task) id=" + id +
                ", Наименование = " + name +
                ", Описание = " + description +
                ", Статус = " + status.toString() +
                ", дата/время начала = " + startTime.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)) +
                ", продолжительность " + String.format("%d", duration.toMinutes()) + " минут";
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        } else {
           return startTime.plusMinutes(duration.toMinutes());
        }
    }

    @Override
    public int compareTo(Task o) {
        return this.startTime.compareTo(o.startTime);
    }
}
