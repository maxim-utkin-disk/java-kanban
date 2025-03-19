package http.handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.GlobalSettings;

import java.io.IOException;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(GlobalSettings.DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendCreated(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.sendResponseHeaders(406, 0);
        h.close();
    }


    protected void sendResultWithText(HttpExchange h, int errCode, String errMsg)  {
        try {
            if (!errMsg.isEmpty() || !errMsg.isBlank()) {
                byte[] resp = errMsg.getBytes(GlobalSettings.DEFAULT_CHARSET);
                h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
                h.sendResponseHeaders(errCode, resp.length);
                h.getResponseBody().write(resp);
            } else {
                h.sendResponseHeaders(errCode, 0);
            }
            h.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void handleInternal(HttpExchange exchange) throws IOException {
    }
    ;

    protected void tryHandle(HttpExchange h) {
        try {
            handleInternal(h);
        } catch (Exception ex) {
            sendResultWithText(h, 500, ex.getMessage());
        }
    }

}
