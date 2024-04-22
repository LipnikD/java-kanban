package ru.lipnik.taskmanager.service.handlers;

import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.Task;
import ru.lipnik.taskmanager.service.HttpTaskServerTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest extends HttpTaskServerTest {

    public PrioritizedHandlerTest() throws IOException {
        super();
    }

    @Test
    public void getPrioritizedHandler() throws IOException, InterruptedException {

        Task task1 = new Task(taskManager.newId(), "Task 1", "Описание 1 задачи",
                LocalDateTime.now(), 10);
        Task task2 = new Task(taskManager.newId(), "Task 2", "Описание 2 задачи",
                LocalDateTime.now().plusHours(1), 10);
        Task task3 = new Task(taskManager.newId(), "Task 3", "Описание 3 задачи",
                LocalDateTime.now().minusHours(1), 10);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        TreeSet<Task> sortedTasks = taskManager.getPrioritizedTasks();
        String prioritizedTasksToJson = gson.toJson(sortedTasks);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Некорректный код ответа при получении приоритезированного списка задач");
        assertEquals(prioritizedTasksToJson, response.body(), "Некорректный приоритезированный список задач.");
    }
}