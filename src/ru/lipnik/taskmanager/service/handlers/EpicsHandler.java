package ru.lipnik.taskmanager.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.service.TaskManager;

import java.io.IOException;

public class EpicsHandler extends Handler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RequestDescription description = getEndpointDescription(exchange);
        int id = description.getId();
        Epic epic;

        try (exchange) {
            switch (description.getEndpoint()) {
                case GET_EPICS:
                    writeResponse(exchange, 200, gson.toJson(taskManager.getEpics()));
                    break;
                case GET_EPIC_BY_ID:
                    epic = taskManager.getEpic(id);
                    if (epic == null) {
                        exchange.sendResponseHeaders(404, -1);
                    } else {
                        writeResponse(exchange, 200, gson.toJson(epic));
                    }
                    break;
                case CREATE_EPIC:
                    epic = gson.fromJson(readRequest(exchange), Epic.class);
                    taskManager.addEpic(epic);
                    writeResponse(exchange, 201, gson.toJson(epic.getId()));
                    break;
                case DELETE_EPIC:
                    taskManager.deleteEpic(id);
                    writeResponse(exchange, 200, gson.toJson(id));
                    break;
                case UNKNOWN:
                    exchange.sendResponseHeaders(404, -1);
            }
        } catch (Throwable exception) {
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
