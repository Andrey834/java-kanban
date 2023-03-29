package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.SimpleTask;
import tasks.StatusTask;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, SimpleTask> simpleTasksMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTasksMap = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTasksMap = new HashMap<>();
    private Integer counterId = 0;

    public void saveSimpleTask(SimpleTask simpleTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        simpleTask.setId(id);
        simpleTask.setStatus(StatusTask.NEW);
        getSimpleTasksMap().put(id, simpleTask);
    }

    public void saveEpicTask(EpicTask epicTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        epicTask.setId(id);
        ArrayList<Integer> subTaskList = new ArrayList<>();
        epicTask.setSubTaskList(subTaskList);
        epicTask.setStatus(StatusTask.NEW);
        getEpicTasksMap().put(id, epicTask);
    }

    public void saveSubTask(SubTask subTask) {
        setCounterId(getCounterId() + 1);
        Integer id = getCounterId();
        subTask.setId(id);
        subTask.setStatus(StatusTask.NEW);
        getSubTasksMap().put(id, subTask);
        getEpicTasksMap().get(subTask.getOwnEpic()).getSubTaskList().add(id);
    }

    public void updateSimpleTask(SimpleTask simpleTask) {
        if (getSimpleTasksMap().get(simpleTask.getId()) != null) {
            getSimpleTasksMap().put(simpleTask.getId(), simpleTask);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (getEpicTasksMap().get(epicTask.getId()) != null) {
            epicTask.setStatus(getEpicTasksMap().get(epicTask.getId()).getStatus());
            epicTask.setSubTaskList(getEpicTasksMap().get(epicTask.getId()).getSubTaskList());
            getEpicTasksMap().put(epicTask.getId(), epicTask);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (getSubTasksMap().get(subTask.getId()) != null) {
            subTask.setOwnEpic(getSubTasksMap().get(subTask.getId()).getOwnEpic());
            getSubTasksMap().put(subTask.getId(), subTask);

            if (subTask.getStatus() != StatusTask.NEW) {
                getEpicTasksMap().get(subTask.getOwnEpic()).setStatus(StatusTask.IN_PROGRESS);
            }

            int listSubDone = 0;
            for (Integer subT : getEpicTasksMap().get(subTask.getOwnEpic()).getSubTaskList()) {
                if (getSubTasksMap().get(subT).getStatus() == StatusTask.DONE) {
                    listSubDone = listSubDone + 1;
                }
            }
            if (getEpicTasksMap().get(subTask.getOwnEpic()).getSubTaskList().size() == listSubDone) {
                getEpicTasksMap().get(subTask.getOwnEpic()).setStatus(StatusTask.DONE);
            }
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public void removeTask(Integer id) {
        if (getSimpleTasksMap().get(id) != null) {
            getSimpleTasksMap().remove(id);
        } else if (getEpicTasksMap().get(id) != null) {
            for (Integer subId : getEpicTasksMap().get(id).getSubTaskList()) {
                getSubTasksMap().remove(subId);
            }
            getEpicTasksMap().remove(id);
        } else if (getSubTasksMap().get(id) != null) {
            getEpicTasksMap().get(getSubTasksMap().get(id).getOwnEpic()).getSubTaskList().remove(id);
            getSubTasksMap().remove(id);
        } else {
            System.out.println("Ошибка");
        }
    }

    public void removeAllTasks() {
        getSimpleTasksMap().clear();
        getSubTasksMap().clear();
        getEpicTasksMap().clear();
    }

    public void getTask(Integer id) {
        try {
            if (getSimpleTasksMap().get(id) != null) {
                System.out.println(getSimpleTasksMap().get(id));
            } else if (getEpicTasksMap().get(id) != null) {
                System.out.println(getEpicTasksMap().get(id));
            } else if (getSubTasksMap().get(id) != null) {
                System.out.println(getSubTasksMap().get(id));
            } else {
                System.out.println("Задача не найдена");
            }
        } catch (Exception exception) {
            System.out.println("Ошибка");
        }
    }

    public void getSubListFromEpic(Integer id) {
        if (getEpicTasksMap().get(id) != null) {
            if (getEpicTasksMap().get(id).getSubTaskList().isEmpty()) {
                System.out.println("Отсутсвуют Суб-Задачи");
            } else {
                for (Integer subTask : getEpicTasksMap().get(id).getSubTaskList()) {
                    System.out.println(getSubTasksMap().get(subTask));
                }
            }
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public HashMap<Integer, SimpleTask> getSimpleTasksMap() {
        return simpleTasksMap;
    }

    public HashMap<Integer, SubTask> getSubTasksMap() {
        return subTasksMap;
    }

    public HashMap<Integer, EpicTask> getEpicTasksMap() {
        return epicTasksMap;
    }
}