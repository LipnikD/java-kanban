package ru.lipnik.taskmanager.service.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lipnik.taskmanager.service.HttpTaskServer;
import ru.lipnik.taskmanager.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class Handler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson;

    public Handler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    public RequestDescription getEndpointDescription(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String request;
        String subRequest = "";
        String parameter = "";
        int id = 0;
        boolean get = method.equals("GET");
        boolean post = method.equals("POST");
        boolean delete = method.equals("DELETE");

        if (!(get || post || delete) || pathParts.length < 2) {
            return new RequestDescription();
        }

        request = pathParts[1];
        if (pathParts.length == 3) {
            parameter = pathParts[2];
        } else if (pathParts.length == 4) {
            parameter = pathParts[2];
            subRequest = pathParts[3];
        }

        if (!parameter.isEmpty()) {
            try {
                id = Integer.parseInt(parameter);
            } catch (NumberFormatException e) {
                return new RequestDescription();
            }
        }

        switch (request) {
            case "history":
                if (get) {
                    return new RequestDescription(Endpoint.GET_HISTORY);
                }
            case "prioritized":
                if (get) {
                    return new RequestDescription(Endpoint.GET_PRIORITIZED_TASKS);
                }
            case "tasks":
                if (get && id != 0) {
                    return new RequestDescription(Endpoint.GET_TASK_BY_ID, id);
                } else if (get) {
                    return new RequestDescription(Endpoint.GET_TASKS);
                } else if (post && id != 0) {
                    return new RequestDescription(Endpoint.UPDATE_TASK, id);
                } else if (post) {
                    return new RequestDescription(Endpoint.CREATE_TASK);
                } else if (id != 0) {
                    return new RequestDescription(Endpoint.DELETE_TASK, id);
                }
            case "subtasks":
                if (get && id != 0) {
                    return new RequestDescription(Endpoint.GET_SUBTASK_BY_ID, id);
                } else if (get) {
                    return new RequestDescription(Endpoint.GET_SUBTASKS);
                } else if (post && id != 0) {
                    return new RequestDescription(Endpoint.CREATE_SUBTASK, id);
                } else if (delete && id != 0) {
                    return new RequestDescription(Endpoint.DELETE_SUBTASK, id);
                }
            case "epics":
                if (get && id != 0 && subRequest.equals("subtasks")) {
                    return new RequestDescription(Endpoint.GET_EPIC_SUBTASKS, id);
                } else if (get && id != 0) {
                    return new RequestDescription(Endpoint.GET_EPIC_BY_ID, id);
                } else if (get) {
                    return new RequestDescription(Endpoint.GET_EPICS);
                } else if (post && id == 0) {
                    return new RequestDescription(Endpoint.CREATE_EPIC);
                } else if (delete && id != 0) {
                    return new RequestDescription(Endpoint.DELETE_EPIC, id);
                }
        }

        return new RequestDescription();
    }

    protected String readRequest(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void writeResponse(HttpExchange exchange, int responseCode, String responseString) throws IOException {

        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, response.length);
        exchange.getResponseBody().write(response);
    }

    public abstract void handle(HttpExchange exchange) throws IOException;

}
