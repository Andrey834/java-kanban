package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    void saveTask(Task task);

    void saveSubTask(SubTask subTask);

    void saveEpicTask(EpicTask epicTask);

    void updateTask(Task task);

    void updateEpicTask(EpicTask epicTask);

    void updateSubTask(SubTask subTask);

    void removeTask(Integer id);

    void removeAllTasks();

    void getTask(Integer id);

    void getSubListFromEpic(Integer id);

    List<Task> getHistory();
}
