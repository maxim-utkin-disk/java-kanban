package http.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.EpicNotFoundException;
import exceptions.TaskTimeExecIntersectTime;
import model.Epic;
import model.Subtask;
import utils.GlobalSettings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        int epicId;
        if (uriPathItems.length == 2) {
            ArrayList<Epic> epicList = taskManager.getEpicList();
            String json = gson.toJson(epicList);
            sendResult(exchange, 200, json);
        } else if (uriPathItems.length == 3) {
            try {
                epicId = Integer.parseInt(uriPathItems[2]);
                Epic e = taskManager.getEpic(epicId);
                String json = gson.toJson(e, Epic.class);
                sendResult(exchange, 200, json);
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить epicId из переданного значения (" + uriPathItems[2] + ")");
            } catch (EpicNotFoundException e) {
                sendResult(exchange, 404, "Не найден эпик epicId=" + uriPathItems[2]);
            }
        } else if (uriPathItems.length == 4) {
            try {
                epicId = Integer.parseInt(uriPathItems[2]);
                Epic e = taskManager.getEpic(epicId);
                ArrayList<Subtask> epicSubtasksList = e.getSubtasksPerEpic();
                String json = gson.toJson(epicSubtasksList);
                sendResult(exchange, 200, json);
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить epicId из переданного значения (" + uriPathItems[2] + ")");
            } catch (EpicNotFoundException e) {
                sendResult(exchange, 404, "Не найден эпик epicId=" + uriPathItems[2]);
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        int epicId;
        JsonObject jsonObject;
        try (InputStreamReader inputStream = new InputStreamReader(exchange.getRequestBody(), GlobalSettings.DEFAULT_CHARSET)) {
            jsonObject = JsonParser.parseReader(inputStream).getAsJsonObject();

            if (!(jsonObject.has("name") &&
                    (jsonObject.has("description"))
            )) {
                sendResult(exchange, 400, "Неверный запрос, проверьте формат данных - не хватает обязательных полей");
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
                    sendResult(exchange, 201, "Эпик id=" + epicId + " успешно обновлен");
                } else {
                    epicId = taskManager.addEpic(e);
                    sendResult(exchange, 200, "Эпик id=" + epicId + " успешно добавлен");
                }
            }
        } catch (TaskTimeExecIntersectTime tteit) {
            sendResult(exchange, 406, tteit.getMessage());
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        if (uriPathItems.length == 3) {
            try {
                final int deletedEpicId = Integer.parseInt(uriPathItems[2]);
                Optional<Epic> deletedEpic = taskManager.getEpicList().stream()
                        .filter(epic -> epic.getId() == deletedEpicId)
                        .findFirst();
                if (deletedEpic.isPresent()) {
                    taskManager.deleteEpic(deletedEpic.get());
                    sendResult(exchange, 200,"Эпик epicId=" + deletedEpicId + "  удален");
                } else {
                    sendResult(exchange, 404, "Не удалено, тк не найден эпик epic_id=" + uriPathItems[2]);
                }
            } catch (NumberFormatException e) {
                sendResult(exchange, 400, "Не удалось вычислить epicId из переданного значения (" + uriPathItems[2] + ")");
            }
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }

}
