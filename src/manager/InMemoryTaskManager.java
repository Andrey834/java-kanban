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
        taskMap.put(id, task);
    }

    @Override
    public void saveSubTask(SubTask subTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        subTask.setId(id);
        subTask.setStatus(StatusTask.NEW);
        subMap.put(id, subTask);
        epicMap.get(subTask.getOwnEpic()).getSubTaskList().add(id);
    }

    @Override
    public void saveEpicTask(EpicTask epicTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        epicTask.setId(id);
        ArrayList<Integer> subTaskList = new ArrayList<>();
        epicTask.setSubTaskList(subTaskList);
        epicTask.setStatus(StatusTask.NEW);
        epicMap.put(id, epicTask);
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.get(task.getId()) != null) {
            taskMap.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (epicMap.get(epicTask.getId()) != null) {
            epicTask.setStatus(epicMap.get(epicTask.getId()).getStatus());
            epicTask.setSubTaskList(epicMap.get(epicTask.getId()).getSubTaskList());
            epicMap.put(epicTask.getId(), epicTask);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subMap.get(subTask.getId()) != null) {
            subTask.setOwnEpic(subMap.get(subTask.getId()).getOwnEpic());
            subMap.put(subTask.getId(), subTask);

            if (subTask.getStatus() != StatusTask.NEW) {
                epicMap.get(subTask.getOwnEpic()).setStatus(StatusTask.IN_PROGRESS);
            }

            int listSubDone = 0;
            for (Integer subT : epicMap.get(subTask.getOwnEpic()).getSubTaskList()) {
                if (subMap.get(subT).getStatus() == StatusTask.DONE) {
                    listSubDone = listSubDone + 1;
                }
            }
            if (epicMap.get(subTask.getOwnEpic()).getSubTaskList().size() == listSubDone) {
                epicMap.get(subTask.getOwnEpic()).setStatus(StatusTask.DONE);
            }
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void removeTask(Integer id) {
        if (taskMap.get(id) != null) {
            taskMap.remove(id);
        } else if (epicMap.get(id) != null) {
            for (Integer subId : epicMap.get(id).getSubTaskList()) {
                subMap.remove(subId);
                historyManager.remove(subId);
            }
            epicMap.remove(id);
        } else if (subMap.get(id) != null) {
            epicMap.get(subMap.get(id).getOwnEpic()).getSubTaskList().remove(id);
            subMap.remove(id);
        } else {
            System.out.println("Ошибка");
        }
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        subMap.clear();
        epicMap.clear();
        historyManager.clear();
    }

    @Override
    public Task getTask(Integer id) {
        if (taskMap.get(id) != null) {
            historyManager.add(taskMap.get(id));
            return taskMap.get(id);
        } else if (epicMap.get(id) != null) {
            historyManager.add(epicMap.get(id));
            return epicMap.get(id);
        } else if (subMap.get(id) != null) {
            historyManager.add(subMap.get(id));
            return subMap.get(id);
        }
        return null;
    }

    @Override
    public ArrayList<Integer> getSubListFromEpic(Integer id) {
        if (epicMap.get(id) != null) {
            return epicMap.get(id).getSubTaskList();
        }
        return null;
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

    public List<Task> getTaskMap() {
        List<Task> tasksList = new ArrayList<Task>(taskMap.values());
        return tasksList;
    }

    public List<SubTask> getSubMap() {
        List<SubTask> subList = new ArrayList<SubTask>(subMap.values());
        return subList;
    }

    public List<EpicTask> getEpicMap() {
        List<EpicTask> epicList = new ArrayList<EpicTask>(epicMap.values());
        return epicList;
    }
}
