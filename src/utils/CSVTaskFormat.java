package utils;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

public class CSVTaskFormat {

    public static String toString(Task task) {
        StringBuilder result = new StringBuilder();
        result.append(task.getId() + ",");
        result.append(task.getTaskType() + ",");
        result.append(task.getName() + ",");
        result.append(task.getStatus() + ",");
        result.append(task.getDescription() + ",");
        if (task.getTaskType() == TaskType.SUBTASK) {
            result.append(((Subtask)task).getEpicId() + ",");
        }
        if (task.getTaskType() != TaskType.EPIC) {
            if (task.getStartTime() != null) {
                result.append(task.getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)) + ",");
            }
            if (task.getDuration() != null) {
                result.append(task.getDuration().toMinutes());
            }

        }
        return result.toString();
    }

    public static String toString(ArrayList<Task> historyManager) {
        StringBuilder result = new StringBuilder("history:");
        for (Task t : historyManager) {
            result.append(t.getId() + ",");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public static Task taskFromString(String value) {
        if (value.isBlank() || value.isBlank()) {
            return null;
        }
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType taskType = TaskType.valueOf(values[1]);
        final TaskState taskState = TaskState.valueOf(values[3]);
        if (taskType == TaskType.TASK) {
            Task result = new Task(values[2], values[4], taskState,
                    LocalDateTime.parse(values[5], DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                    Duration.ofMinutes(Long.parseLong(values[6])));
            result.setId(id);
            return result;
        } else if (taskType == TaskType.EPIC) {
            Epic result = new Epic(values[2], values[4]);
            result.setId(id);
            return result;
        } else if (taskType == TaskType.TASK.SUBTASK) {
            final int epicId = Integer.parseInt(values[5]);
            Subtask result = new Subtask(values[2], values[4], epicId, taskState,
                    LocalDateTime.parse(values[6], DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                    Duration.ofMinutes(Long.parseLong(values[7])));
            result.setId(id);
            return result;
        } else {
            return null;
        }

    }

    public static ArrayList<Integer> historyFromString(String value) {
        if (value.isBlank() || value.isBlank() || !value.startsWith("history:")) {
            return null;
        }
        final String[] values = value.substring("history:".length()).split(",");
        ArrayList<Integer> result = new ArrayList<>();
        for (String numStr : values) {
            result.add(Integer.parseInt(numStr));
        }
        return result;
    }
}
