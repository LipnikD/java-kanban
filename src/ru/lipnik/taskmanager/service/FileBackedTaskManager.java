package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Epic;
import ru.lipnik.taskmanager.model.Status;
import ru.lipnik.taskmanager.model.Subtask;
import ru.lipnik.taskmanager.model.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File dataStorageFile;
    static final String CSV_DELIMITER = ";";
    static final String EMPTY_TIME = "Отсутствует";
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");

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
                String[] dataParts = line.split(CSV_DELIMITER, -7);
                FileRecordType type = FileRecordType.valueOf(dataParts[0]);
                int id = Integer.parseInt(dataParts[1]);
                String name = dataParts[2];
                String description = dataParts[3];
                String status = dataParts[4];
                LocalDateTime startTime;
                if (dataParts[5].isEmpty() || dataParts[5].equals(EMPTY_TIME)) {
                    startTime = null;
                } else {
                    startTime = LocalDateTime.parse(dataParts[5], FORMATTER);
                }
                long duration;
                if (dataParts[6].isEmpty()) {
                    duration = 0;
                } else {
                    duration = Long.parseLong(dataParts[6]);
                }
                switch (type) {
                    case TASK:
                        Task task = new Task(id, name, description);
                        task.setStartTime(startTime);
                        task.setDurationOfMinutes(duration);
                        taskManager.addTask(task);
                        taskManager.setStatus(task, Status.valueOf(status));
                        break;
                    case EPIC:
                        epic = new Epic(id, name, description);
                        taskManager.addEpic(epic);
                        taskManager.setStatus(epic, Status.valueOf(status));
                        break;
                    case SUBTASK:
                        Subtask subtask = new Subtask(id, name, description);
                        subtask.setStartTime(startTime);
                        subtask.setDurationOfMinutes(duration);
                        taskManager.addSubtask(epic, subtask);
                        taskManager.setStatus(subtask, Status.valueOf(status));
                        break;
                    case HISTORY:
                        if (taskManager.getTask(id) == null) {
                            if (taskManager.getEpic(id) == null) {
                                taskManager.getSubtask(id);
                            }
                        }
                        break;
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

            for (Task task : getTasks()) {
                fileWriter.write(getTaskString(FileRecordType.TASK, task));
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(getTaskString(FileRecordType.EPIC, epic));
                for (Subtask subtask : getSubtasks(epic)) {
                    fileWriter.write(getTaskString(FileRecordType.SUBTASK, subtask));
                }
            }

            for (Task task : getHistory()) {
                fileWriter.write(String.join(CSV_DELIMITER,
                        FileRecordType.HISTORY.name(), task.getId().toString(), "", "", "", "", "",
                        System.lineSeparator()));
            }

        } catch (IOException | ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getTaskString(FileRecordType recordType, Task item) {
        String startTime = EMPTY_TIME;
        if (item.getStartTime() != null) {
            startTime = item.getStartTime().format(FORMATTER);
        }
        return String.join(CSV_DELIMITER, recordType.name(), item.getId().toString(), item.getName(),
                item.getDescription(), item.getStatus().name(), startTime,
                String.valueOf(item.getDurationToMinutes()), System.lineSeparator());
    }

    private static String dataHeadersLine() {
        return String.join(CSV_DELIMITER,
                "type", "id", "name", "description", "status", "startTime", "duration");
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