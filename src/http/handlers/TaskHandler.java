package http.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.TaskNotFoundException;
import exceptions.TaskTimeExecIntersectTime;
import model.Task;
import model.TaskState;
import utils.GlobalSettings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        int taskId;

        if (uriPathItems.length == 2) {
            ArrayList<Task> taskList = taskManager.getTaskList();
            String json = gson.toJson(taskList);
            sendResult(exchange, 200, json);
        } else if (uriPathItems.length == 3) {
            try {
                taskId = Integer.parseInt(uriPathItems[2]);
                Task t = taskManager.getTask(taskId);
                String json = gson.toJson(t, Task.class);
                sendResult(exchange, 200, json);
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить taskId из переданного значения (" + uriPathItems[2] + ")");
            } catch (TaskNotFoundException e) {
                sendResult(exchange, 404, "Не найдена задача taskId=" + uriPathItems[2]);
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        int taskId;
        JsonObject jsonObject;
        try (InputStreamReader inputStream = new InputStreamReader(exchange.getRequestBody(), GlobalSettings.DEFAULT_CHARSET)) {
            jsonObject = JsonParser.parseReader(inputStream).getAsJsonObject();

            if (!(jsonObject.has("name") &&
                    (jsonObject.has("description")) &&
                    (jsonObject.has("status")) &&
                    (jsonObject.has("duration")) &&
                    (jsonObject.has("startTime"))
            )) {
                sendResult(exchange, 400, "Неверный запрос, проверьте формат данных - не хватает обязательных полей");
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
                    sendResult(exchange, 201, "Задача id=" + taskId + " успешно обновлена");
                } else {
                    taskId = taskManager.addTask(t);
                    sendResult(exchange, 200, "Задача id=" + taskId + " успешно добавлена");
                }
            }
        } catch (TaskTimeExecIntersectTime tteit) {
            sendResult(exchange, 406, tteit.getMessage());
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        int taskId;
        if (uriPathItems.length == 3) {
            try {
                taskId = Integer.parseInt(uriPathItems[2]);
                taskManager.deleteTask(taskId);
                sendResult(exchange, 200,"Задача taskId=" + taskId + "  удалена");
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить taskId из переданного значения (" + uriPathItems[2] + ")");
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }


}
