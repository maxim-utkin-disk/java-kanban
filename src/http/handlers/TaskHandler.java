package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.TaskNotFoundException;
import exceptions.TaskTimeExecIntersectTime;
import model.Task;
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

public class TaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleInternal(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uriPath = exchange.getRequestURI().getPath();
        String[] uriPathItems = uriPath.split("/");
        Integer taskId;

        switch (method) {
            case "GET":
                    if (uriPathItems.length == 2) {
                        ArrayList<Task> taskList = taskManager.getTaskList();
                        String json = gson.toJson(taskList);
                        sendText(exchange, json);
                    } else if (uriPathItems.length == 3) {
                        try {
                            taskId = Integer.parseInt(uriPathItems[2]);
                            Task t = taskManager.getTask(taskId);
                            String json = gson.toJson(t, Task.class);
                            sendText(exchange, json);
                        } catch (NumberFormatException e) {
                            sendResultWithText(exchange, 400, "Не удалось вычислить taskId из переданного значения (" + uriPathItems[2] + ")");
                        } catch (TaskNotFoundException e) {
                            sendResultWithText(exchange, 404, "Не найдена задача taskId=" + uriPathItems[2]);
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
                            (jsonObject.has("startTime"))
                    )) {
                        sendResultWithText(exchange, 400, "Неверный запрос, проверьте формат данных - не хватает обязательных полей");
                    } else {
                        String name = jsonObject.get("name").getAsString();
                        String description = jsonObject.get("description").getAsString();
                        TaskState state = TaskState.valueOf(jsonObject.get("status").getAsString());
                        LocalDateTime startTime = LocalDateTime.parse(
                                jsonObject.get("startTime").getAsString(),
                                DateTimeFormatter.ofPattern(GlobalSettings.DATETIME_FORMAT_PATTERN));
                        Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());

                        Task t = new Task(name, description, state, startTime, duration);

                        if (jsonObject.has("id")) {
                            taskId = jsonObject.get("id").getAsInt();
                        } else {
                            taskId = -1;
                        }

                        if (taskId != -1) {
                            t.setId(taskId);
                            taskManager.updateTask(t);
                            sendResultWithText(exchange, 201, "Задача id=" + taskId + " успешно обновлена");
                        } else {
                            taskId = taskManager.addTask(t);
                            sendText(exchange, "Задача id=" + taskId + " успешно добавлена");
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
                        taskId = Integer.parseInt(uriPathItems[2]);
                        taskManager.deleteTask(taskId);
                        sendText(exchange, "Задача taskId=" + taskId + "  удалена");
                    } catch (NumberFormatException e) {
                        sendResultWithText(exchange, 400, "Не удалось вычислить taskId из переданного значения (" + uriPathItems[2] + ")");
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
