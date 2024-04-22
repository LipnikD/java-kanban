package ru.lipnik.taskmanager.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.service.TaskManager;

import java.io.IOException;

public class SubtasksHandler extends Handler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RequestDescription description = getEndpointDescription(exchange);
        int id = description.getId();
        Subtask subtask;

        try (exchange) {
            switch (description.getEndpoint()) {
                case GET_SUBTASKS:
                    writeResponse(exchange, 200, gson.toJson(taskManager.getSubtasks()));
                    break;
                case GET_SUBTASK_BY_ID:
                    subtask = taskManager.getSubtask(id);
                    if (subtask == null) {
                        exchange.sendResponseHeaders(404, -1);
                    } else {
                        writeResponse(exchange, 200, gson.toJson(subtask));
                    }
                    break;
                case UPDATE_SUBTASK:
                    subtask = gson.fromJson(readRequest(exchange), Subtask.class);
                    taskManager.updateTask(subtask);
                    writeResponse(exchange, 201, gson.toJson(subtask.getId()));
                    break;
                case CREATE_SUBTASK:
                    subtask = gson.fromJson(readRequest(exchange), Subtask.class);
                    if (taskManager.intersectionDetected(subtask)) {
                        exchange.sendResponseHeaders(406, -1);
                    } else {
                        taskManager.addSubtask(taskManager.getEpic(id), subtask);
                        writeResponse(exchange, 201, gson.toJson(subtask.getId()));
                    }
                    break;
                case DELETE_SUBTASK:
                    taskManager.deleteSubtask(id);
                    writeResponse(exchange, 200, gson.toJson(id));
                    break;
                case UNKNOWN:
                    exchange.sendResponseHeaders(404, -1);
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка ввода-вывода при ответе: " + exception.getMessage());
            exchange.sendResponseHeaders(500, -1);
        } catch (Exception exception) {
            System.out.println("Произошла ошибка при ответе: " + exception.getMessage());
            exchange.sendResponseHeaders(500, -1);
        } catch (Throwable exception) {
            System.out.println("Ошибка: " + exception.getMessage());
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
