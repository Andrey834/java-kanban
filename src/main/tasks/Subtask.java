package main.tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int ownEpic;

    public Subtask(String title,
                   String description,
                   long duration,
                   String startTime,
                   int ownEpic) {
        super(title, description, duration, startTime);
        this.ownEpic = ownEpic;
        this.setType(TypeTask.SUBTASK);
        this.setStatus(StatusTask.NEW);
    }

    public Subtask(int id,
                   TypeTask type,
                   String title,
                   String description,
                   StatusTask status,
                   long duration,
                   LocalDateTime startTime,
                   int ownEpic) {
        super(id, type, title, description, status, duration, startTime);
        this.ownEpic = ownEpic;
        this.setType(TypeTask.SUBTASK);
    }

    public Subtask(String title, String description, int ownEpic) {
        super(title, description);
        this.ownEpic = ownEpic;
        this.setType(TypeTask.SUBTASK);
        if (this.getStatus() == null) {
            this.setStatus(StatusTask.NEW);
        }
    }

    public Subtask(int id, long duration, String startTime) {
        super(id, duration, startTime);
        this.setType(TypeTask.SUBTASK);
    }

    public Subtask(int id, StatusTask status) {
        super(id, status);
        this.setType(TypeTask.SUBTASK);
    }

    public void addIdSubtaskInEpicList(main.tasks.Epic epic) {
        epic.getIdListSubtasks().add(this.getId());
    }

    public void removeIdSubtaskInEpicList(Epic epic) {
        epic.getIdListSubtasks().remove((Integer) this.getId());
    }

    public void whenAddSubChangeStartTimeAndDurationEpic(main.tasks.Epic epic) {
        if (this.getStartTime().isBefore(epic.getStartTime())) {
            epic.setStartTime(this.getStartTime());
        }
        epic.setDuration(epic.getDuration() + this.getDuration());
    }

    public void whenUpdateSubChangeStartTimeAndDurationEpic(main.tasks.Epic epic, Subtask oldSubtask) {
        if (this.getStartTime().isBefore(epic.getStartTime())) {
            epic.setStartTime(this.getStartTime());
        }
        epic.setDuration((epic.getDuration() - oldSubtask.getDuration()) + this.getDuration());
    }

    @Override
    public void substitutionOfOldValuesOnUpdate(Task oldTask) {
        super.substitutionOfOldValuesOnUpdate(oldTask);
        Subtask oldSub = (Subtask) oldTask;
        this.setOwnEpic(oldSub.getOwnEpic());
    }

    public int getOwnEpic() {
        return ownEpic;
    }

    public void setOwnEpic(int ownEpic) {
        this.ownEpic = ownEpic;
    }

    @Override
    public String toString() {
        return getId() +
                "," + getType() +
                "," + getTitle() +
                "," + getStatus() +
                "," + getDescription() +
                "," + getDuration() +
                "," + getStartTime() +
                "," + getOwnEpic();
    }
}
