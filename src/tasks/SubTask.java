package tasks;

public class SubTask extends SimpleTask {
    private Integer ownEpic;

    public SubTask(Integer id, String title, String description, StatusTask status) {
        super(id, title, description, status);
    }

    public SubTask(String title, String description, Integer ownEpic) {
        super(title, description);
        this.ownEpic = ownEpic;
    }

    public Integer getOwnEpic() {
        return ownEpic;
    }

    public void setOwnEpic(Integer ownEpic) {
        this.ownEpic = ownEpic;
    }

    @Override
    public String toString() {
        return "{SUB id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", ownEpic=" + ownEpic +
                '}';
    }
}