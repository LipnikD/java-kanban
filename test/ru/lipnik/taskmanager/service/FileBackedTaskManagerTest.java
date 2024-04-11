package ru.lipnik.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final File file = new File("./resources/Test.csv");

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void readFromEmptyFileTest() {
        Task task = new Task(taskManager.newId(), "Task", "Task description",
                LocalDateTime.now(), 100);
        Epic epic = new Epic(taskManager.newId(), "Epic", "Epic description");
        Subtask subtask = new Subtask(taskManager.newId(), "Subtask", "Subtask description",
                LocalDateTime.now().plusDays(1), 100);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "История должна быть пустой");
        Assertions.assertTrue(fromFileManager.getHistory().isEmpty(), "История должна быть пустой");

        Assertions.assertEquals(taskManager.getTask(task.getId()), fromFileManager.getTask(task.getId()),
                "Задача не совпадает.");
        Assertions.assertEquals(taskManager.getEpic(epic.getId()), fromFileManager.getEpic(epic.getId()),
                "Эпик не совпадает.");
        Assertions.assertEquals(taskManager.getSubtask(subtask.getId()), fromFileManager.getSubtask(subtask.getId()),
                "Подзадача не совпадает.");
    }

    @Test
    public void readFromFileTest(){
        Task task = new Task(taskManager.newId(), "Task", "Task description",
                LocalDateTime.now(), 100);
        Epic epic = new Epic(taskManager.newId(), "Epic", "Epic description");
        Subtask subtask = new Subtask(taskManager.newId(), "Subtask", "Subtask description",
                LocalDateTime.now().plusDays(1), 100);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask);

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        Task task2 = new Task(taskManager.newId(), "Task2", "Task2 description");
        taskManager.addTask(task2);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(taskManager.getHistory(), fromFileManager.getHistory(),
                "Обнаружено расхождение при проверке сохраненной истории.");
        Assertions.assertEquals(taskManager.getTask(task.getId()), fromFileManager.getTask(task.getId()),
                "Задачи не совпадают.");
        Assertions.assertEquals(taskManager.getEpic(epic.getId()), fromFileManager.getEpic(epic.getId()),
                "Содержимое epic не соответствует.");
        Assertions.assertEquals(taskManager.getSubtask(subtask.getId()), fromFileManager.getSubtask(subtask.getId()),
                "Содержимое epic не соответствует.");
    }

    @Test
    public void saveToFileTest() {
        Task task = new Task(taskManager.newId(), "Task", "Task description");
        Epic epic = new Epic(taskManager.newId(), "Epic", "Epic description");
        Subtask subtask = new Subtask(taskManager.newId(), "Subtask", "Subtask description",
                LocalDateTime.now().plusDays(1), 100);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(taskManager.getTasks(), fromFileManager.getTasks(), "Задачи не совпадают.");
        Assertions.assertEquals(taskManager.getEpics(), fromFileManager.getEpics(), "Эпики не совпадают.");
        Assertions.assertEquals(taskManager.getSubtasks(epic), fromFileManager.getSubtasks(epic), "Подзадачи не совпадают.");
    }

    @Test
    public void workWithWrongFile() {
        FileBackedTaskManager wrongFileManager = new FileBackedTaskManager(new File("resources/wrongFile.csv"));
        Task task = new Task(taskManager.newId(), "Task", "Test description");

        assertThrows(ManagerSaveException.class, () -> wrongFileManager.addTask(task),
                "Сохранение данных в несуществующий файл недопустимо.");

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(String.join(";", FileRecordType.TASK.name(), task.getId().toString(),
                    System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(ManagerRestoreException.class, () -> FileBackedTaskManager.loadFromFile(file),
                "Чтение файла с некорректным составом данных недопустимо.");

    }

    @AfterEach
    public void clearTestCSVFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
