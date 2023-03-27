package tasks;

import java.util.ArrayList;

public class EpicTask extends SimpleTask{
    private ArrayList<Integer> subTaskList;

    public EpicTask(Integer id, String title, StatusTask status, ArrayList<Integer> subTaskList) {
        super(id, title, status);
        this.subTaskList = subTaskList;
    }

    public EpicTask(Integer id, String title, String description, StatusTask status) {
        super(id, title, description, status);
    }

    public EpicTask(Integer id, String title, String description) {
        super(id, title, description);
    }

    public EpicTask(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(ArrayList<Integer> subTaskList) {
        this.subTaskList = subTaskList;
    }

    @Override
    public String toString() {
        return "{EPIC id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() +
                ", subTaskList=" + subTaskList +
                '}';
    }
}
