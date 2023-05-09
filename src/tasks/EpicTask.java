package tasks;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private List<Integer> idSubList;

    public EpicTask(int id, String title, String description, StatusTask status, List<Integer> idSubList) {
        super(id, title, description, status);
        this.idSubList = idSubList;
    }

    public EpicTask(int id, String title, String description, StatusTask status) {
        super(id, title, description, status);
    }

    public EpicTask(String title, String description, List<Integer> idSubList) {
        super(title, description);
        this.idSubList = idSubList;
    }

    public EpicTask(String title, String description) {
        super(title, description);
    }

    public List<Integer> getIdSubList() {
        return idSubList;
    }

    public void setIdSubList(List<Integer> idSubList) {
        this.idSubList = idSubList;
    }

    @Override
    public String toString() {
        return getId() + "," +
                "EPICTASK" + "," +
                getTitle() + "," +
                getStatus() + "," +
                getDescription() + ",";
    }
}
