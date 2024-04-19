package ru.lipnik.taskmanager.service.handlers;

import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.*;
import ru.lipnik.taskmanager.service.HttpTaskServerTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest extends HttpTaskServerTest {

    public TasksHandlerTest() throws IOException {
        super();
    }

    @Test
    public void addTask() throws IOException, InterruptedException {

        Task task = new Task(taskManager.newId(), "Task", "Описание задачи",
                LocalDateTime.now().minusDays(1), 1);

        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа при добавлении задачи.");

        Task newTask = taskManager.getTask(task.getId());

        assertNotNull(newTask, "Задача не добавлена.");
        assertEquals(task.getName(), newTask.getName(), "Некорректное наименование задачи.");
        assertEquals(roundToStartOfMinute(task.getEndTime()), newTask.getEndTime(),
                "Некорректный момент времени завершения задачи.");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {

        Task task = new Task(taskManager.newId(), "Task", "Описание задачи",
                LocalDateTime.now().minusDays(1), 1);
        taskManager.addTask(task);

        String updatedTaskName = "Обновленное имя задачи";
        task.setName(updatedTaskName);

        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа при обновлении задачи.");

        assertEquals(updatedTaskName, taskManager.getTask(task.getId()).getName(),
                "Некорректное наименование задачи после обновления.");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {

        Task task = new Task(taskManager.newId(), "Task", "Описание задачи",
                LocalDateTime.now().minusDays(1), 1);
        taskManager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа при удалении задачи.");
        assertNull(taskManager.getTask(task.getId()), "Задача не удалена.");
    }

    @Test
    public void addTaskWithBadTime() throws IOException, InterruptedException {

        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        Task task = new Task(taskManager.newId(), "Task", "Описание", dateTime, 1);
        taskManager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks");

        Task sameTimeTask = new Task(taskManager.newId(), "Task", "Описание", dateTime, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sameTimeTask)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(),
                "Некорректный код ответа при добавлении пересекающейся по времени задачи.");

        Task sameTimeNewTask = taskManager.getTask(sameTimeTask.getId());

        assertNull(sameTimeNewTask, "Пересекающаяся по времени задача не должна быть добавлена.");
    }
}