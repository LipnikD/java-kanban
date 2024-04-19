package ru.lipnik.taskmanager.service.handlers;

import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.service.HttpTaskServerTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest extends HttpTaskServerTest {

    public SubtasksHandlerTest() throws IOException {
        super();
    }

    @Test
    public void addSubtaskToEpic() throws IOException, InterruptedException {

        Epic epic = new Epic(taskManager.newId(), "Epic", "Описание эпика");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(taskManager.newId(), "Subtask", "Описание подзадачи");

        URI url = URI.create("http://localhost:8080/subtasks/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа при добавлении подзадачи.");

        Subtask newSubtask = taskManager.getSubtask(subtask.getId());

        assertNotNull(newSubtask, "Подзадача не добавлена.");
        assertEquals(subtask.getName(), newSubtask.getName(), "Некорректное наименование добавленной подзадачи.");
        assertEquals(subtask.getDescription(), newSubtask.getDescription(),
                "Некорректное описание добавленной подзадачи.");
    }
}