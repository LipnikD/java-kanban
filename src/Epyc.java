import java.util.ArrayList;

public class Epyc extends Task {
    private final ArrayList<Subtask> subtasks;

    Epyc(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (subtasks.contains(subtask)) {
            return;
        }
        subtasks.add(subtask);
        updateStatus();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
        }
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allSubtasksNew = false;
            }
        }
        if (allSubtasksNew) {
            this.setStatus(Status.NEW);
        } else if (allSubtasksDone) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtask == null || subtask.getClass() != Subtask.class) {
            return;
        }
        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return "Epyc{" +
                super.toString() +
                ", subtasks=" + subtasks +
                "} ";
    }
}
