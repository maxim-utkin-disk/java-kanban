package http.handlers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.DurationAdapter;
import utils.GlobalSettings;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    protected Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected void sendResult(HttpExchange exchange, int resCode, String resMsg)  {
        try {
            if (!resMsg.isEmpty() || !resMsg.isBlank()) {
                byte[] resp = resMsg.getBytes(GlobalSettings.DEFAULT_CHARSET);
                exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
                exchange.sendResponseHeaders(resCode, resp.length);
                exchange.getResponseBody().write(resp);
            } else {
                exchange.sendResponseHeaders(resCode, 0);
            }
            exchange.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected String[] getUriItems(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }

    protected void processGet(HttpExchange exchange) throws IOException {
        sendResult(exchange, 400, "Данный метод не предусмотрен");
    }
    protected void processPost(HttpExchange exchange) throws IOException {
        sendResult(exchange, 400, "Данный метод не предусмотрен");
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        sendResult(exchange, 400, "Данный метод не предусмотрен");
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    processGet(exchange);
                    break;
                case "POST":
                    processPost(exchange);
                    break;
                case "DELETE":
                    processDelete(exchange);
                    break;
                default:
                    sendResult(exchange, 400, "Данный метод не предусмотрен");
            }
        } catch (Exception ex) {
            sendResult(exchange, 500, ex.getMessage());
        }


    }

}
