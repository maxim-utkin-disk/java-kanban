package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        if (uriPathItems.length == 2) {
            ArrayList<Task> historyList = taskManager.getHistory();
            String json = gson.toJson(historyList);
            sendResult(exchange, 200, json);
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }

}
