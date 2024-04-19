package ru.lipnik.taskmanager.service.handlers;

import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.Task;
import ru.lipnik.taskmanager.service.HttpTaskServerTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends HttpTaskServerTest {

    public HistoryHandlerTest() throws IOException {
        super();
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {

        Task task1 = new Task(taskManager.newId(), "Task 1", "Описание 1 задачи");
        Task task2 = new Task(taskManager.newId(), "Task 2", "Описание 2 задачи");
        Task task3 = new Task(taskManager.newId(), "Task 3", "Описание 3 задачи");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());

        List<Task> history = taskManager.getHistory();
        String historyToJson = gson.toJson(history);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа при получении истории");
        assertEquals(historyToJson, response.body(), "Состав истории не соответствует.");
    }
}