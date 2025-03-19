package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.EpicNotFoundException;
import exceptions.TaskTimeExecIntersectTime;
import model.Epic;
import model.Subtask;
import utils.DurationAdapter;
import utils.GlobalSettings;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handleInternal(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uriPath = exchange.getRequestURI().getPath();
        String[] uriPathItems = uriPath.split("/");
        Integer epicId;

        switch (method) {
            case "GET":
                if (uriPathItems.length == 2) {
                    ArrayList<Epic> epicList = taskManager.getEpicList();
                    String json = gson.toJson(epicList);
                    sendText(exchange, json);
                } else if (uriPathItems.length == 3) {
                    try {
                        epicId = Integer.parseInt(uriPathItems[2]);
                        Epic e = taskManager.getEpic(epicId);
                        String json = gson.toJson(e, Epic.class);
                        sendText(exchange, json);
                    } catch (NumberFormatException e) {
                        sendResultWithText(exchange, 400, "Не удалось вычислить epicId из переданного значения (" + uriPathItems[2] + ")");
                    } catch (EpicNotFoundException e) {
                        sendResultWithText(exchange, 404, "Не найден эпик epicId=" + uriPathItems[2]);
                    }
                } else if (uriPathItems.length == 4) {
                    try {
                        epicId = Integer.parseInt(uriPathItems[2]);
                        Epic e = taskManager.getEpic(epicId);
                        ArrayList<Subtask> epicSubtasksList = e.getSubtasksPerEpic();
                        String json = gson.toJson(epicSubtasksList);
                        sendText(exchange, json);
                    } catch (NumberFormatException e) {
                        sendResultWithText(exchange, 400, "Не удалось вычислить epicId из переданного значения (" + uriPathItems[2] + ")");
                    } catch (EpicNotFoundException e) {
                        sendResultWithText(exchange, 404, "Не найден эпик epicId=" + uriPathItems[2]);
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
                            (jsonObject.has("description"))
                    )) {
                        sendResultWithText(exchange, 400, "Неверный запрос, проверьте формат данных - не хватает обязательных полей");
                    } else {
                        String name = jsonObject.get("name").getAsString();
                        String description = jsonObject.get("description").getAsString();

                        Epic e = new Epic(name, description);

                        if (jsonObject.has("id")) {
                            epicId = jsonObject.get("id").getAsInt();
                        } else {
                            epicId = -1;
                        }

                        if (epicId != -1) {
                            e.setId(epicId);
                            taskManager.updateEpic(e);
                            sendResultWithText(exchange, 201, "Эпик id=" + epicId + " успешно обновлен");
                        } else {
                            epicId = taskManager.addEpic(e);
                            sendText(exchange, "Эпик id=" + epicId + " успешно добавлен");
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
                        final int deletedEpicId = Integer.parseInt(uriPathItems[2]);
                        Optional<Epic> deletedEpic = taskManager.getEpicList().stream()
                                .filter(epic -> epic.getId() == deletedEpicId)
                                .findFirst();
                        if (deletedEpic.isPresent()) {
                            taskManager.deleteEpic(deletedEpic.get());
                            sendText(exchange, "Эпик epicId=" + deletedEpicId + "  удален");
                        } else {
                            sendResultWithText(exchange, 404, "Не удалено, тк не найден эпик epic_id=" + uriPathItems[2]);
                        }
                    } catch (NumberFormatException e) {
                        sendResultWithText(exchange, 400, "Не удалось вычислить epicId из переданного значения (" + uriPathItems[2] + ")");
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
