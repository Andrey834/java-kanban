package tasks;

public class SubTask extends Task{
    private int ownEpic;

    public SubTask(int id, String title, String description, StatusTask status, int ownEpic) {
        super(id, title, description, status);
        this.ownEpic = ownEpic;
    }

    public SubTask(int id, String title, String description, StatusTask status) {
        super(id, title, description, status);
    }

    public SubTask(String title, String description, int ownEpic) {
        super(title, description);
        this.ownEpic = ownEpic;
    }

    public SubTask(int id, StatusTask status) {
        super(id, status);
    }

    public int getOwnEpic() {
        return ownEpic;
    }

    public void setOwnEpic(int ownEpic) {
        this.ownEpic = ownEpic;
    }

    @Override
    public String toString() {
        return getId() + "," +
                "SUBTASK" + "," +
                getTitle() + "," +
                getStatus() + "," +
                getDescription() + "," +
                getOwnEpic();


    }
}
