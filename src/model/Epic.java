package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtasksPerEpic;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskState.NEW, null, null);
        subtasksPerEpic = new HashMap<>();
    }

    public ArrayList<Subtask> getSubtasksPerEpic() {
        return new ArrayList<>(subtasksPerEpic.values());
    }

    public void linkSubtaskToEpic(Subtask s) {
        if (!subtasksPerEpic.containsKey(s.getId())) {
            subtasksPerEpic.put(s.getId(), s);
        }
        actualizeStatus();
        actualizeEpicExecutionPeriod();
    }

    public void unlinkSubtaskFromEpic(Subtask s) {
        subtasksPerEpic.remove(s.getId(), s);
        actualizeStatus();
        actualizeEpicExecutionPeriod();
    }

    public void unlinkAllSubtasksFromEpic() {
        subtasksPerEpic.clear();
        actualizeStatus();
        actualizeEpicExecutionPeriod();
    }

    public void printEpic() {
        System.out.println(this);
        System.out.println("Подзадачи эпика (всего " + subtasksPerEpic.size() + " позиций):");
        subtasksPerEpic.values().stream()
                .forEach(System.out::println);

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
         result.append("Эпик (epic) id=" + getId());
         result.append(", Наименование = " + getName());
         result.append(", Описание = " + getDescription());
         result.append(", Статус = " + getStatus().toString());
         result.append(", кол-во подзадач = " + subtasksPerEpic.size());
         if (this.getStartTime() != null) {
             result.append(", дата-время начала " + getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)));
         }
         if (this.getDuration() != null) {
             result.append(", продолжительность " + String.format("%d", getDuration().toMinutes()) + " минут");
         }
        return result.toString();
    }

    public void actualizeStatus() {
        if (subtasksPerEpic.isEmpty()) {
            this.status = TaskState.NEW;
        } else {
            int cntStatusNew = 0;
            int cntStatusDone = 0;

            cntStatusNew = (int)subtasksPerEpic.values().stream()
                    .filter(subtask -> (subtask.getStatus() == TaskState.NEW))
                    .count();

            cntStatusDone = (int)subtasksPerEpic.values().stream()
                    .filter(subtask -> (subtask.getStatus() == TaskState.DONE))
                    .count();

            if (cntStatusNew == subtasksPerEpic.size()) {
                this.status = TaskState.NEW;
            } else if (cntStatusDone == subtasksPerEpic.size()) {
                this.status = TaskState.DONE;
            } else {
                this.status = TaskState.IN_PROGRESS;
            }
        }
    }

    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void actualizeEpicExecutionPeriod() {
        if (subtasksPerEpic.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ofMinutes(0L);
            this.endTime = null;
        } else {
            Optional<Subtask> startSubtask = subtasksPerEpic.values().stream()
                    .min(Comparator.comparing(Task::getStartTime));

            Optional<Subtask> endSubtask = subtasksPerEpic.values().stream()
                    .max(Comparator.comparing(Task::getEndTime));

            startSubtask.ifPresent(subtask -> this.startTime = subtask.getStartTime());
            endSubtask.ifPresent(subtask -> this.endTime = subtask.getEndTime());

            Duration calculatedDuration = subtasksPerEpic.values().stream()
                    .filter(subtask -> subtask.getDuration() != null)
                    .map(subtask -> subtask.getDuration())
                    .reduce(Duration.ZERO, Duration::plus);

            this.duration = calculatedDuration;

        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

}
