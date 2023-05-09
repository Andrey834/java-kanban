package managers;

import tasks.*;

import java.util.List;

public interface TaskManager {
    void createTask(Task task);
    void createEpicTask(EpicTask epicTask);
    void createSubTask(SubTask subTask);
    void updateTask(Task task);
    void updateEpicTask(EpicTask epicTask);
    void updateSubTask(SubTask subTask);
    void removeTask(Integer id);
    void removeAllTasks();
    Task getTask(Integer id);
    List<Integer> getSubListFromEpic(Integer id);
    List<Task> getHistory();
    List<Task> getListAllTasks();
}
