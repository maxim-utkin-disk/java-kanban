package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Task;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoryHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handleInternal(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uriPath = exchange.getRequestURI().getPath();
        String[] uriPathItems = uriPath.split("/");
        Integer taskId;

        switch (method) {
            case "GET":
                if (uriPathItems.length == 2) {
                    ArrayList<Task> historyList = taskManager.getHistory();
                    String json = gson.toJson(historyList);
                    sendText(exchange, json);
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
