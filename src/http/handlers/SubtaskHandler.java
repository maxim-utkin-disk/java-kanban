package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.SubtaskNotFoundException;
import exceptions.TaskTimeExecIntersectTime;
import model.Subtask;
import model.TaskState;
import utils.DurationAdapter;
import utils.GlobalSettings;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SubtaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handleInternal(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uriPath = exchange.getRequestURI().getPath();
        String[] uriPathItems = uriPath.split("/");
        Integer subtaskId;

        switch (method) {
            case "GET":
                if (uriPathItems.length == 2) {
                    ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
                    String json = gson.toJson(subtaskList);
                    sendText(exchange, json);
                } else if (uriPathItems.length == 3) {
                    try {
                        subtaskId = Integer.parseInt(uriPathItems[2]);
                        Subtask s = taskManager.getSubtask(subtaskId);
                        String json = gson.toJson(s, Subtask.class);
                        sendText(exchange, json);
                    } catch (NumberFormatException e) {
                        sendResultWithText(exchange, 400, "Не удалось вычислить subtaskId из переданного значения (" + uriPathItems[2] + ")");
                    } catch (SubtaskNotFoundException e) {
                        sendResultWithText(exchange, 404, "Не найдена подзадача subtaskId=" + uriPathItems[2]);
                    }
                } else {
                    sendResultWithText(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
                }
                break;
            case "POST":
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
                        sendResultWithText(exchange, 400, "Неверный запрос, проверьте формат данных - не хватает обязательных полей");
                    } else {
                        Integer epicId = jsonObject.get("epicId").getAsInt();
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
                            sendResultWithText(exchange, 201, "Подзадача subtaskId=" + subtaskId + " успешно обновлена");
                        } else {
                            subtaskId = taskManager.addSubtask(s);
                            sendText(exchange, "Подзадача subtaskId=" + subtaskId + " успешно добавлена");
                        }
                    }
                } catch (TaskTimeExecIntersectTime tteit) {
                    sendResultWithText(exchange, 406, tteit.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "DELETE":
                if (uriPathItems.length == 3) {
                    try {
                        subtaskId = Integer.parseInt(uriPathItems[2]);
                        taskManager.deleteSubtask(subtaskId);
                        sendText(exchange, "Подзадача subtaskId=" + subtaskId + "  удалена");
                    } catch (NumberFormatException e) {
                        sendResultWithText(exchange, 400, "Не удалось вычислить  subtaskId из переданного значения (" + uriPathItems[2] + ")");
                    }
                } else {
                    sendResultWithText(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
                }
                break;
            default: sendResultWithText(exchange, 405, "Недопустимый метод");
        }
    }

    @Override
    public void handle(HttpExchange exchange) {
        tryHandle(exchange);
    }

}
