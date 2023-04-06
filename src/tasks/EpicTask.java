package tasks;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> subTaskList;

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