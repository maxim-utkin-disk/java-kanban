package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String[] uriPathItems = getUriItems(exchange);
        if (uriPathItems.length == 2) {
            ArrayList<Task> prioritezedList = taskManager.getPrioritizedTasks();
            String json = gson.toJson(prioritezedList);
            sendResult(exchange, 200, json);
        } else {
            sendResult(exchange, 400, "Неверный запрос, уточните кол-во аргументов");
        }
    }

}
