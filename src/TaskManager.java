import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epyc> epycs;
    private final HashMap<Integer, Subtask> subtasks;

    TaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epycs = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public Task createTask(String name, String description) {
        Task task = new Task(newId(), name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epyc createEpyc(String name, String description) {
        Epyc epic = new Epyc(newId(), name, description);
        epycs.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubTask(Epyc epyc, String name, String description) {
        if (epyc == null || epyc.getClass() != Epyc.class) {
            return null;
        }
        Subtask subtask = new Subtask(newId(), name, description);
        epyc.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (int key : tasks.keySet()) {
            result.add(tasks.get(key));
        }
        return result;
    }

    public ArrayList<Epyc> getEpycs() {
        ArrayList<Epyc> result = new ArrayList<>();
        for (int key : epycs.keySet()) {
            result.add(epycs.get(key));
        }
        return result;
    }

    public ArrayList<Subtask> getSubtasks(Epyc epyc) {
        if (epyc == null || epyc.getClass() != Epyc.class) {
            return null;
        }
        return epyc.getSubtasks();
    }

    public void updateTask(Task task) {
        if (task == null || task.getClass() != Task.class) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getClass() != Subtask.class) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpyc(Epyc epyc) {
        if (epyc == null || epyc.getClass() != Epyc.class) {
            return;
        }
        epycs.put(epyc.getId(), epyc);
    }

    public void deleteTask(Task task) {
        if (task == null || task.getClass() != Task.class) {
            return;
        }
        tasks.remove(task.getId());
    }

    public void deleteEpyc(Epyc epyc) {
        if (epyc == null || epyc.getClass() != Epyc.class) {
            return;
        }
        if (epycs.containsKey(epyc.getId()) && epyc.getSubtasks().isEmpty()) {
            epycs.remove(epyc.getId());
        }
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtask == null || subtask.getClass() != Subtask.class) {
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            getSubtaskEpyc(subtask).deleteSubtask(subtask);
            subtasks.remove(subtask.getId());
        }
    }

    public void setStatus(Object issue, Status status) {
        if (issue.getClass() == Task.class) {
            ((Task) issue).setStatus(status);
        } else if (issue.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) issue;
            subtask.setStatus(status);
            getSubtaskEpyc(subtask).updateStatus();
        }
    }

    private Epyc getSubtaskEpyc(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        for (int key : epycs.keySet()) {
            Epyc epyc = epycs.get(key);
            if (epyc.getSubtasks().contains(subtask)) {
                return epyc;
            }
        }
        return null;
    }

    private int newId() {
        return id++;
    }
}
