package main.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Epic extends Task {
    private List<Integer> idListSubtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.setType(TypeTask.EPIC);
        if (this.getIdListSubtasks() == null) {
            this.setIdListSubtasks(new ArrayList<>());
        }
        if (this.getStatus() == null) {
            this.setStatus(StatusTask.NEW);
        }
    }

    public Epic(int id,
                String title,
                String description,
                StatusTask status,
                long duration,
                LocalDateTime startTime,
                List<Integer> idListSubtasks) {
        super(id, title, description, status, duration, startTime);
        this.idListSubtasks = idListSubtasks;
        this.setType(TypeTask.EPIC);
    }

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.setType(TypeTask.EPIC);
    }

    @Override
    public void substitutionOfOldValuesOnUpdate(Task oldTask) {
        super.substitutionOfOldValuesOnUpdate(oldTask);
        if (this.getIdListSubtasks() == null) {
            Epic oldEpic = (Epic) oldTask;
            this.setIdListSubtasks(oldEpic.getIdListSubtasks());
        }
    }

    public void checkStatusSubFromEpic(Map<Integer, Subtask> subtaskMap) {
        final long counterDoneSub;
        final long counterNewSub;
        final int idSubListSize = this.getIdListSubtasks().size();
        counterDoneSub = this.getIdListSubtasks().stream().map(subtaskMap::get).
                map(Subtask::getStatus).filter(Predicate.isEqual(StatusTask.DONE)).count();
        counterNewSub = this.getIdListSubtasks().stream().map(subtaskMap::get).
                map(Subtask::getStatus).filter(Predicate.isEqual(StatusTask.NEW)).count();
        if (idSubListSize == counterNewSub) {
            this.setStatus(StatusTask.NEW);
        } else if (idSubListSize == counterDoneSub) {
            this.setStatus(StatusTask.DONE);
        } else {
            this.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(Subtask subtask) {
        if (this.endTime == null || subtask.getEndTime().isAfter(this.endTime)) {
            this.endTime = subtask.getEndTime();
        }
    }

    public void whenUpdateSubtaskUpdateEndTimeEpic(Subtask oldSubtask, Subtask newSubtask) {
        if (this.endTime.equals(oldSubtask.getEndTime())) {
            this.endTime = newSubtask.getEndTime();
        }
    }

    public List<Integer> getIdListSubtasks() {
        return idListSubtasks;
    }

    public void setIdListSubtasks(List<Integer> idListSubtasks) {
        this.idListSubtasks = idListSubtasks;
    }

    @Override
    public String toString() {
        return getId() +
                "," + getType() +
                ",'" + getTitle() + '\'' +
                "," + getStatus() +
                ",'" + getDescription() + '\'' +
                "," + getDuration() +
                "," + getStartTime();
    }
}
