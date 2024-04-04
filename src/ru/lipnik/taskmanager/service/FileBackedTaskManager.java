package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Status;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File dataStorageFile;
    static final String CSV_DELIMITER = ";";

    public FileBackedTaskManager() {
        super();
        this.dataStorageFile = new File("./resources/Tasks.csv");
    }

    public FileBackedTaskManager(File file) {
        super();
        this.dataStorageFile = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerRestoreException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Epic epic = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line == null || !line.equals(dataHeadersLine())) {
                throw new ManagerRestoreException("Некорректный формат файла хранения данных");
            }
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] dataParts = line.split(CSV_DELIMITER, -5);
                FileRecordType type = FileRecordType.valueOf(dataParts[0]);
                int id = Integer.parseInt(dataParts[1]);
                String name = dataParts[2];
                String description = dataParts[3];
                String status = dataParts[4];
                switch (type) {
                    case FileRecordType.TASK:
                        Task task = new Task(id, name, description);
                        taskManager.addTask(task);
                        taskManager.setStatus(task, Status.valueOf(status));
                        break;
                    case FileRecordType.EPIC:
                        epic = new Epic(id, name, description);
                        taskManager.addEpic(epic);
                        taskManager.setStatus(epic, Status.valueOf(status));
                    case FileRecordType.SUBTASK:
                        Subtask subtask = new Subtask(id, name, description);
                        taskManager.addSubtask(epic, subtask);
                        taskManager.setStatus(subtask, Status.valueOf(status));
                    case FileRecordType.HISTORY:
                        if (taskManager.getTask(id) == null) {
                            if (taskManager.getEpic(id) == null) {
                                taskManager.getSubtask(id);
                            }
                        }
                    default:
                        throw new ManagerRestoreException("Некорректный тип записи в файле данных: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return taskManager;
    }

    public void save() {

        if (!dataStorageFile.canWrite()) {
            throw new ManagerSaveException("Файл " + dataStorageFile.getAbsolutePath() + " недоступен для записи.");
        }

        try (Writer fileWriter = new FileWriter(dataStorageFile)) {

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