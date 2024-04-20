package ru.lipnik.taskmanager.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.lipnik.taskmanager.model.Task;
import ru.lipnik.taskmanager.service.TaskManager;

import java.io.IOException;

public class TasksHandler extends Handler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        RequestDescription description = getEndpointDescription(exchange);
        int id = description.getId();
        Task task;

        try (exchange) {
            switch (description.getEndpoint()) {
                case GET_TASKS:
                    writeResponse(exchange, 200, gson.toJson(taskManager.getTasks()));
                    break;
                case GET_TASK_BY_ID:
                    task = taskManager.getTask(id);
                    if (task == null) {
                        exchange.sendResponseHeaders(404, -1);
                    } else {
                        writeResponse(exchange, 200, gson.toJson(task));
                    }
                    break;
                case UPDATE_TASK:
                    task = gson.fromJson(readRequest(exchange), Task.class);
                    taskManager.updateTask(task);
                    writeResponse(exchange, 201, gson.toJson(task.getId()));
                    break;
                case CREATE_TASK:
                    task = gson.fromJson(readRequest(exchange), Task.class);
                    if (taskManager.intersectionDetected(task)) {
                        exchange.sendResponseHeaders(406, -1);
                    } else {
                        taskManager.addTask(task);
                        writeResponse(exchange, 201, gson.toJson(task.getId()));
                    }
                    break;
                case DELETE_TASK:
                    taskManager.deleteTask(id);
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
