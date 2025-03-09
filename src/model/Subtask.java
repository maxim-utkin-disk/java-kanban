package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, TaskState status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Подзадача (subtask) id=" + getId() +
                ", Наименование = " + getName() +
                ", Описание = " + getDescription() +
                ", Статус = " + getStatus().toString() +
                ", epicId = " + epicId +
                ", дата/время начала = " + getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)) +
                ", продолжительность " + String.format("%d", getDuration().toMinutes()) + " минут";
    }

    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
}
