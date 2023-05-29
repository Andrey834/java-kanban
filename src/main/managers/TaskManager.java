package main.managers;

import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    void addNewTask(Task task);
    void addNewEpic(Epic epicTask);
    void addNewSubtask(Subtask subTask);
    void updateTask(Task task);
    void updateEpic(Epic epicTask);
    void updateSubtask(Subtask subTask);
    void removeTask(Integer id);
    void removeEpic(Integer id);
    void removeSubtask(Integer id);
    void removeAllTasks();
    Task getTask(Integer id);
    Epic getEpic(Integer id);
    Subtask getSubtask(Integer id);
    List<Integer> getSubListFromEpic(Integer id);
    List<Task> getHistory();
    List<Task> getListAllTasks();
    List<Task> getPrioritizedTasks();
    HashMap<Integer, Task> getTaskMap();
    HashMap<Integer, Epic> getEpicMap();
    HashMap<Integer, Subtask> getSubMap();
}


