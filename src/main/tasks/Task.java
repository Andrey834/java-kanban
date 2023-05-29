package main.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private StatusTask status;
    private TypeTask type;
    private long duration;
    private final LocalDateTime DEFAULT_DATE = LocalDateTime.of(9999,1,1,0,0);
    private LocalDateTime startTime = DEFAULT_DATE;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public Task(String title, String description, long duration, String startTime) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.type = TypeTask.TASK;
        if (this.status == null) {
            this.status = StatusTask.NEW;
        }
    }

    public Task(int id, String title, String description, StatusTask status, long duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.type = TypeTask.TASK;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.type = TypeTask.TASK;
        if (this.status == null) {
            this.status = StatusTask.NEW;
        }
    }

    public Task(int id, StatusTask status) {
        this.id = id;
        this.status = status;
        this.setType(TypeTask.TASK);
    }

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.setType(TypeTask.TASK);
        if (this.status == null) {
            this.status = StatusTask.NEW;
        }
    }

    public Task(int id, long duration, String startTime) {
        this.id = id;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.setType(TypeTask.TASK);
    }

    public void substitutionOfOldValuesOnUpdate(Task oldTask) {
        if (this.getTitle() == null) {
            this.setTitle(oldTask.getTitle());
        }
        if (this.getDescription() == null) {
            this.setDescription(oldTask.getDescription());
        }
        if (this.getStatus() == null) {
            this.setStatus(oldTask.getStatus());
        }
        if (this.getDuration() == 0) {
            this.setDuration(oldTask.getDuration());
        }
        if (this.getStartTime() == null || this.getStartTime() == startTime) {
            this.setStartTime(oldTask.getStartTime());
        }
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plusMinutes(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public TypeTask getType() {
        return type;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, type);
    }

    @Override
    public String toString() {
        return id +
                "," + type +
                ",'" + title + '\'' +
                "," + status +
                ",'" + description + '\'' +
                "," + duration +
                "," + startTime;
    }
}
