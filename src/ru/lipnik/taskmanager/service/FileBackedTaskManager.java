package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Status;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File dataStorageFile;
    public static String CSV_DELIMITER = ";";

    public FileBackedTaskManager() {
        super();
        this.dataStorageFile = new File("./resources/Tasks.csv");
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        Epic epic = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line == null || !line.equals(dataHeadersLine())) {
                throw new ManagerRestoreException("Некорректный формат файла хранения данных");
            }
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] dataParts = line.split(CSV_DELIMITER, -5);
                String type = dataParts[0];
                int id = Integer.parseInt(dataParts[1]);
                String name = dataParts[2];
                String description = dataParts[3];
                String status = dataParts[4];
                if (type.equals(FileRecordType.TASK.name())) {
                    Task task = new Task(id, name, description);
                    taskManager.addTask(task);
                    taskManager.setStatus(task, Status.valueOf(status));
                } else if (type.equals(FileRecordType.EPIC.name())) {
                    epic = new Epic(id, name, description);
                    taskManager.addEpic(epic);
                    taskManager.setStatus(epic, Status.valueOf(status));
                } else if (type.equals(FileRecordType.SUBTASK.name())) {
                    Subtask subtask = new Subtask(id, name, description);
                    taskManager.addSubtask(epic, subtask);
                    taskManager.setStatus(subtask, Status.valueOf(status));
                } else if (type.equals(FileRecordType.HISTORY.name())) {
                    taskManager.getTask(id);
                    taskManager.getEpic(id);
                    taskManager.getSubtask(id);
                } else {
                    throw new ManagerRestoreException("Некорректный тип записи в файле данных: " + line);
                }
            }
        } catch (IOException | ManagerSaveException e) {
            System.out.println(e.getMessage());
        } catch (ManagerRestoreException e) {
            System.out.println("Ошибка чтения файла хранения данных : " + e.getMessage());
        }
        return taskManager;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(dataStorageFile)) {

            if (!dataStorageFile.canWrite()) {
                throw new ManagerSaveException("Файл " + dataStorageFile.getAbsolutePath() + " недоступен для записи.");
            }

            fileWriter.write(dataHeadersLine() + System.lineSeparator());

            for (Task item : getTasks()) {
                fileWriter.write(String.join(CSV_DELIMITER,
                        FileRecordType.TASK.name(), item.getId().toString(), item.getName(),
                        item.getDescription(), item.getStatus().name(), System.lineSeparator()));
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(String.join(CSV_DELIMITER,
                        FileRecordType.EPIC.name(), epic.getId().toString(), epic.getName(),
                        epic.getDescription(), epic.getStatus().name(), System.lineSeparator()));
                for (Subtask subtask : getSubtasks(epic)) {
                    fileWriter.write(String.join(CSV_DELIMITER,
                            FileRecordType.SUBTASK.name(), subtask.getId().toString(), subtask.getName(),
                            subtask.getDescription(), subtask.getStatus().name(), System.lineSeparator()));
                }
            }

            for (Task task : getHistory()) {
                fileWriter.write(String.join(CSV_DELIMITER,
                        FileRecordType.HISTORY.name(), task.getId().toString(), "", "", "",
                        System.lineSeparator()));
            }

        } catch (IOException | ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String dataHeadersLine() {
        return String.join(CSV_DELIMITER, "type", "id", "name", "description", "status");
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        super.addSubtask(epic, subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void setStatus(Object issue, Status status) {
        super.setStatus(issue, status);
        save();
    }
}