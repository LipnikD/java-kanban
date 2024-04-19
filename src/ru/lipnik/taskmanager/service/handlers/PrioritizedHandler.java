package ru.lipnik.taskmanager.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.lipnik.taskmanager.service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends Handler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        RequestDescription description = getEndpointDescription(exchange);

        try (exchange) {
            if (description.getEndpoint() == Endpoint.GET_PRIORITIZED_TASKS) {
                    writeResponse(exchange, 200, gson.toJson(taskManager.getPrioritizedTasks()));
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        }
    }
}
