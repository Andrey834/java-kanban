package tasks;

public class SimpleTask{
    private Integer id;
    private String title;
    private String description;
    private StatusTask status;

    public SimpleTask(Integer id, String title, String description, StatusTask status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public SimpleTask(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public SimpleTask(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public SimpleTask(Integer id, String title, StatusTask status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public SimpleTask(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        return "{TASK id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                "}\n";
    }
}
