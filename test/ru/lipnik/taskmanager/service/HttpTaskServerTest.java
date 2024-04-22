package ru.lipnik.taskmanager.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDateTime;

public abstract class HttpTaskServerTest {

    protected TaskManager taskManager;
    protected HttpTaskServer taskServer;
    protected HttpClient httpClient;
    protected final Gson gson;

    public HttpTaskServerTest() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        httpClient = HttpClient.newHttpClient();
    }

    @BeforeEach
    void setUp() {

        Task task1 = new Task(taskManager.newId(), "Task 1", "Task one description",
                LocalDateTime.now(), 100);
        Task task2 = new Task(taskManager.newId(), "Task 2", "Task two description",
                LocalDateTime.now().plusDays(1), 100);
        Epic epic = new Epic(taskManager.newId(), "Epic3", "Epic description");
        Subtask subtask1 = new Subtask(taskManager.newId(),"Subtask 1", "Subtask one description",
                LocalDateTime.now().plusDays(2), 600);
        Subtask subtask2 = new Subtask(taskManager.newId(),"Subtask 2", "Subtask two description",
                LocalDateTime.now().plusDays(2).plusHours(10), 60);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);

        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    public LocalDateTime roundToStartOfMinute(LocalDateTime dateTime) {

        return dateTime.withDayOfMonth(dateTime.getDayOfMonth())
                .withHour(dateTime.getHour())
                .withMinute(dateTime.getMinute())
                .withSecond(0)
                .withNano(0);
    }
}