package http;

import com.sun.net.httpserver.HttpServer;
import controllers.TaskManager;
import http.handlers.*;
import utils.GlobalSettings;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(GlobalSettings.PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

    }

    public void HttpTaskServerStart() {
        System.out.println("HttpTaskServer started on port = " + GlobalSettings.PORT);
        httpServer.start();
    }

    public void HttpTaskServerStop() {
        System.out.println("HttpTaskServer stopped");
        httpServer.stop(0);
    }


}
