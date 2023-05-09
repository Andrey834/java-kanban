package tasks;

public class Task {
    private int id;
    private String title;
    private String description;
    private StatusTask status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(int id, StatusTask status) {
        this.id = id;
        this.status = status;
    }

    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String title, String description, StatusTask status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
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

    @Override
    public String toString() {
        return id + "," +
                "TASK" + "," +
                title + "," +
                status + "," +
                description + ",";
    }
}
