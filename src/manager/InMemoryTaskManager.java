package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicMap = new HashMap<>();
    private Integer counterId = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void saveTask(Task task) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        task.setId(id);
        task.setStatus(StatusTask.NEW);
        getTaskMap().put(id, task);
    }

    @Override
    public void saveSubTask(SubTask subTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        subTask.setId(id);
        subTask.setStatus(StatusTask.NEW);
        getSubMap().put(id, subTask);
        getEpicMap().get(subTask.getOwnEpic()).getSubTaskList().add(id);
    }

    @Override
    public void saveEpicTask(EpicTask epicTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        epicTask.setId(id);
        ArrayList<Integer> subTaskList = new ArrayList<>();
        epicTask.setSubTaskList(subTaskList);
        epicTask.setStatus(StatusTask.NEW);
        getEpicMap().put(id, epicTask);
    }

    @Override
    public void updateTask(Task task) {
        if (getTaskMap().get(task.getId()) != null) {
            getTaskMap().put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (getEpicMap().get(epicTask.getId()) != null) {
            epicTask.setStatus(getEpicMap().get(epicTask.getId()).getStatus());
            epicTask.setSubTaskList(getEpicMap().get(epicTask.getId()).getSubTaskList());
            getEpicMap().put(epicTask.getId(), epicTask);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (getSubMap().get(subTask.getId()) != null) {
            subTask.setOwnEpic(getSubMap().get(subTask.getId()).getOwnEpic());
            getSubMap().put(subTask.getId(), subTask);

            if (subTask.getStatus() != StatusTask.NEW) {
                getEpicMap().get(subTask.getOwnEpic()).setStatus(StatusTask.IN_PROGRESS);
            }

            int listSubDone = 0;
            for (Integer subT : getEpicMap().get(subTask.getOwnEpic()).getSubTaskList()) {
                if (getSubMap().get(subT).getStatus() == StatusTask.DONE) {
                    listSubDone = listSubDone + 1;
                }
            }
            if (getEpicMap().get(subTask.getOwnEpic()).getSubTaskList().size() == listSubDone) {
                getEpicMap().get(subTask.getOwnEpic()).setStatus(StatusTask.DONE);
            }
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void removeTask(Integer id) {
        if (getTaskMap().get(id) != null) {
            getTaskMap().remove(id);
        } else if (getEpicMap().get(id) != null) {
            for (Integer subId : getEpicMap().get(id).getSubTaskList()) {
                getSubMap().remove(subId);
                historyManager.remove(subId);
            }
            getEpicMap().remove(id);
        } else if (getSubMap().get(id) != null) {
            getEpicMap().get(getSubMap().get(id).getOwnEpic()).getSubTaskList().remove(id);
            getSubMap().remove(id);
        } else {
            System.out.println("Ошибка");
        }
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        getTaskMap().clear();
        getSubMap().clear();
        getEpicMap().clear();
        historyManager.clear();
    }

    @Override
    public void getTask(Integer id) {
        if (getTaskMap().get(id) != null) {
            System.out.println(getTaskMap().get(id));
            historyManager.add(getTaskMap().get(id));
        } else if (getEpicMap().get(id) != null) {
            System.out.println(getEpicMap().get(id));
            historyManager.add(getEpicMap().get(id));
        } else if (getSubMap().get(id) != null) {
            System.out.println(getSubMap().get(id));
            historyManager.add(getSubMap().get(id));
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void getSubListFromEpic(Integer id) {
        if (getEpicMap().get(id) != null) {
            if (getEpicMap().get(id).getSubTaskList().isEmpty()) {
                System.out.println("Отсутсвуют Суб-Задачи");
            } else {
                for (Integer subTask : getEpicMap().get(id).getSubTaskList()) {
                    System.out.println(getSubMap().get(subTask));
                }
            }
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, SubTask> getSubMap() {
        return subMap;
    }

    public HashMap<Integer, EpicTask> getEpicMap() {
        return epicMap;
    }
}
