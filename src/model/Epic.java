package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdsList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskState.NEW, null, null);
        subtaskIdsList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdsList() {
        return subtaskIdsList;
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIdsList.contains(subtaskId)) {
            subtaskIdsList.add(subtaskId);
        }
    }

    public void deleteSubtaskId(int subtaskId) {
        if (!subtaskIdsList.contains(subtaskId)) {
            return;
        }
        for (int i = subtaskIdsList.size() - 1; i >= 0; i--) {
            if (subtaskIdsList.get(i) == subtaskId) {
                subtaskIdsList.remove(i);
            }
        }
    }

    public void printEpic(ArrayList<Subtask> subtaskList) {
        System.out.println(this);
        System.out.println("Подзадачи эпика (всего " + subtaskIdsList.size() + " позиций):");
        for (Subtask s : subtaskList) {
            if (s.getEpicId() == this.getId())  {
                System.out.println(s);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
         result.append("Эпик (epic) id=" + getId());
         result.append(", Наименование = " + getName());
         result.append(", Описание = " + getDescription());
         result.append(", Статус = " + getStatus().toString());
         result.append(", кол-во подзадач = " + subtaskIdsList.size());
         if (this.getStartTime() != null) {
             result.append(getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)));
         }
         if (this.getDuration() != null) {
             result.append(", продолжительность " + String.format("%d", getDuration().toMinutes()) + " минут");
         }
        /*return "Эпик (epic) id=" + getId() +
                ", Наименование = " + getName() +
                ", Описание = " + getDescription() +
                ", Статус = " + getStatus().toString() +
                ", кол-во подзадач = " + subtaskIdsList.size() +
                ", дата/время начала = " + getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)) +
                ", продолжительность " + String.format("%d", getDuration().toMinutes()) + " минут";*/
        return result.toString();
    }

    public void actualizeStatus(ArrayList<Subtask> subtaskList) {
        if (subtaskIdsList.isEmpty()) {
            this.status = TaskState.NEW;
        } else {
            int cntStatusNew = 0;
            int cntStatusDone = 0;
            /*for (int i = 0; i < subtaskList.size(); i++) {
                Subtask s = subtaskList.get(i);
                if (s.getEpicId() == this.getId()) {
                    if (s.getStatus() == TaskState.NEW) {
                        cntStatusNew++;
                    }
                    if (s.getStatus() == TaskState.DONE) {
                        cntStatusDone++;
                    }
                }
            }*/

            cntStatusNew = (int)subtaskList.stream()
                    .filter(subtask -> ((subtask.getEpicId() == this.getId()) && (subtask.getStatus() == TaskState.NEW)))
                    .count();

            cntStatusDone = (int)subtaskList.stream()
                    .filter(subtask -> ((subtask.getEpicId() == this.getId()) && (subtask.getStatus() == TaskState.DONE)))
                    .count();

            if (cntStatusNew == subtaskIdsList.size()) {
                this.status = TaskState.NEW;
            } else if (cntStatusDone == subtaskIdsList.size()) {
                this.status = TaskState.DONE;
            } else {
                this.status = TaskState.IN_PROGRESS;
            }
        }
    }

    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void actualizeEpicExecutionPeriod(ArrayList<Subtask> subtaskList) {
        if (subtaskIdsList.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ofMinutes(0L);
            this.endTime = null;
        } else {
            //LocalDateTime minSubtaskStartTime = null;
            //LocalDateTime maxSubtaskEndTime = null;


            /*for (int i = 0; i < subtaskList.size(); i++) {
                Subtask s = subtaskList.get(i);
                if (s.getEpicId() == this.getId()) {
                    if (s.getStartTime() != null) {
                        if (minSubtaskStartTime == null) {
                            minSubtaskStartTime = s.getStartTime();
                        } else {
                            if (s.getStartTime().isBefore(minSubtaskStartTime)) {
                                minSubtaskStartTime = s.getStartTime();
                            }
                        }
                        if (maxSubtaskEndTime == null) {
                            maxSubtaskEndTime = s.getEndTime();
                        } else {
                            if (s.getEndTime().isAfter(maxSubtaskEndTime)) {
                                maxSubtaskEndTime = s.getEndTime();
                            }
                        }
                    }

                }
            }*/

            Optional<Subtask> startSubtask = subtaskList.stream()
                    .filter(subtask -> subtask.getEpicId() == this.getId())
                    .min(Comparator.comparing(Task::getStartTime));

            /*
            Optional<Subtask> startSubtask = subtaskList.stream()
                    .filter(subtask -> subtask.getEpicId() == this.getId())
                    .min((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()));*/

            Optional<Subtask> endSubtask = subtaskList.stream()
                    .filter(subtask -> subtask.getEpicId() == this.getId())
                    .max(Comparator.comparing(Task::getEndTime));

            startSubtask.ifPresent(subtask -> this.startTime = subtask.getStartTime());
            endSubtask.ifPresent(subtask -> this.endTime = subtask.getEndTime());

            //this.startTime = minSubtaskStartTime;
            //this.endTime = maxSubtaskEndTime;

            if (startSubtask.isPresent() && endSubtask.isPresent()) {
                this.duration = Duration.between(startSubtask.get().getStartTime(),
                                                 endSubtask.get().getEndTime());
            } else {
                this.duration = null;
            }

            //this.duration = Duration.between(minSubtaskStartTime, maxSubtaskEndTime);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

}
