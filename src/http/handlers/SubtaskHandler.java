package http.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.SubtaskNotFoundException;
import exceptions.TaskTimeExecIntersectTime;
import model.Subtask;
import model.TaskState;
import utils.GlobalSettings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        int subtaskId;
        if (uriPathItems.length == 2) {
            ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
            String json = gson.toJson(subtaskList);
            sendResult(exchange, 200, json);
        } else if (uriPathItems.length == 3) {
            try {
                subtaskId = Integer.parseInt(uriPathItems[2]);
                Subtask s = taskManager.getSubtask(subtaskId);
                String json = gson.toJson(s, Subtask.class);
                sendResult(exchange, 200, json);
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить subtaskId из переданного значения (" + uriPathItems[2] + ")");
            } catch (SubtaskNotFoundException e) {
                sendResult(exchange, 404, "Не найдена подзадача subtaskId=" + uriPathItems[2]);
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
        if (uriPathItems.length == 2) {
            ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
            String json = gson.toJson(subtaskList);
            sendResult(exchange, 200, json);
        } else if (uriPathItems.length == 3) {
            try {
                subtaskId = Integer.parseInt(uriPathItems[2]);
                Subtask s = taskManager.getSubtask(subtaskId);
                String json = gson.toJson(s, Subtask.class);
                sendResult(exchange, 200, json);
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить subtaskId из переданного значения (" + uriPathItems[2] + ")");
            } catch (SubtaskNotFoundException e) {
                sendResult(exchange, 404, "Не найдена подзадача subtaskId=" + uriPathItems[2]);
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        int subtaskId;
        JsonObject jsonObject;
        try (InputStreamReader inputStream = new InputStreamReader(exchange.getRequestBody(), GlobalSettings.DEFAULT_CHARSET)) {
            jsonObject = JsonParser.parseReader(inputStream).getAsJsonObject();

            if (!(jsonObject.has("name") &&
                    (jsonObject.has("description")) &&
                    (jsonObject.has("status")) &&
                    (jsonObject.has("duration")) &&
                    (jsonObject.has("startTime")) &&
                    (jsonObject.has("epicId"))
            )) {
                sendResult(exchange, 400, "Неверный запрос, проверьте формат данных - не хватает обязательных полей");
            } else {
                int epicId = jsonObject.get("epicId").getAsInt();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                TaskState state = TaskState.valueOf(jsonObject.get("status").getAsString());
                LocalDateTime startTime = LocalDateTime.parse(
                        jsonObject.get("startTime").getAsString(),
                        DateTimeFormatter.ofPattern(GlobalSettings.DATETIME_FORMAT_PATTERN));
                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());

                Subtask s = new Subtask(name, description, epicId, state, startTime, duration);

                if (jsonObject.has("id")) {
                    subtaskId = jsonObject.get("id").getAsInt();
                } else {
                    subtaskId = -1;
                }

                if (subtaskId != -1) {
                    s.setId(subtaskId);
                    taskManager.updateSubtask(s);
                    sendResult(exchange, 201, "Подзадача subtaskId=" + subtaskId + " успешно обновлена");
                } else {
                    subtaskId = taskManager.addSubtask(s);
                    sendResult(exchange, 200, "Подзадача subtaskId=" + subtaskId + " успешно добавлена");
                }
            }
        } catch (TaskTimeExecIntersectTime tteit) {
            sendResult(exchange, 406, tteit.getMessage());
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        int subtaskId;
        if (uriPathItems.length == 3) {
            try {
                subtaskId = Integer.parseInt(uriPathItems[2]);
                taskManager.deleteSubtask(subtaskId);
                sendResult(exchange, 200, "Подзадача subtaskId=" + subtaskId + "  удалена");
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить  subtaskId из переданного значения (" + uriPathItems[2] + ")");
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }
}
