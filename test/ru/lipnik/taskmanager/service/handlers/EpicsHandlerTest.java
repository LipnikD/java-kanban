package ru.lipnik.taskmanager.service.handlers;

import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.*;
import ru.lipnik.taskmanager.service.HttpTaskServerTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest extends HttpTaskServerTest {

    public EpicsHandlerTest() throws IOException {
        super();
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {

        Epic epic = new Epic(taskManager.newId(), "Epic", "Описание эпика");

        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа при добавлении эпика.");

        Epic newEpic = taskManager.getEpic(epic.getId());

        assertNotNull(newEpic, "Эпик не добавлен.");
        assertEquals(epic.getName(), newEpic.getName(), "Некорректное наименование добавленного эпика.");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {

        Epic epic = new Epic(taskManager.newId(), "Epic", "Описание эпика");
        taskManager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа при удалении эпика.");
        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удален.");
    }
}