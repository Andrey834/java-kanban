package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static final HashMap<Integer, Task> taskMap = new HashMap<>();
    private static final HashMap<Integer, EpicTask> epicMap = new HashMap<>();
    private static final HashMap<Integer, SubTask> subMap = new HashMap<>();
    private static int counterId = 0;

    @Override
    public void createTask(Task task) {
        {
            task.setId(++counterId);
            task.setStatus(StatusTask.NEW);
            taskMap.put(counterId, task);
        }
    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        {
            epicTask.setId(++counterId);
            epicTask.setStatus(StatusTask.NEW);
            epicTask.setIdSubList(new ArrayList<>());
            epicMap.put(counterId, epicTask);
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (epicMap.containsKey(subTask.getOwnEpic())) {
            {
                subTask.setId(++counterId);
                subTask.setStatus(StatusTask.NEW);
                epicMap.get(subTask.getOwnEpic()).getIdSubList().add(counterId);
                subMap.put(counterId, subTask);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            Task oldTask = taskMap.get(task.getId());
            if (task.getTitle() == null) {
                task.setTitle(oldTask.getTitle());
            }
            if (task.getDescription() == null) {
                task.setDescription(oldTask.getDescription());
            }
            if (task.getStatus() == null) {
                task.setStatus(oldTask.getStatus());
            }
            taskMap.put(task.getId(), task);
            InMemoryHistoryManager.updateNode(task);
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (epicMap.containsKey(epicTask.getId())) {
            EpicTask oldEpic = epicMap.get(epicTask.getId());
            if (epicTask.getTitle() == null) {
                epicTask.setTitle(oldEpic.getTitle());
            }
            if (epicTask.getDescription() == null) {
                epicTask.setDescription(oldEpic.getDescription());
            }
            epicTask.setStatus(oldEpic.getStatus());
            epicMap.put(epicTask.getId(), epicTask);
            InMemoryHistoryManager.updateNode(epicTask);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {

        if (subMap.containsKey(subTask.getId())) {
            SubTask oldSub = subMap.get(subTask.getId());
            if (subTask.getTitle() == null) {
                subTask.setTitle(oldSub.getTitle());
            }
            if (subTask.getDescription() == null) {
                subTask.setDescription(oldSub.getDescription());
            }
            if (subTask.getStatus() == null) {
                subTask.setStatus(oldSub.getStatus());
            }
            subTask.setOwnEpic(oldSub.getOwnEpic());
            subMap.put(subTask.getId(), subTask);
            InMemoryHistoryManager.updateNode(subTask);

            if (subTask.getStatus() != StatusTask.NEW) {
                epicMap.get(subTask.getOwnEpic()).setStatus(StatusTask.IN_PROGRESS);
            }

            int counterDoneTask = 0;
            for (Integer subT : epicMap.get(subTask.getOwnEpic()).getIdSubList()) {
                if (subMap.get(subT).getStatus() == StatusTask.DONE) {
                    counterDoneTask = counterDoneTask + 1;
                }
            }

            if (epicMap.get(subTask.getOwnEpic()).getIdSubList().size() == counterDoneTask) {
                epicMap.get(subTask.getOwnEpic()).setStatus(StatusTask.DONE);
            }
            InMemoryHistoryManager.updateNode(epicMap.get(subTask.getOwnEpic()));
        }
    }

    @Override
    public void removeTask(Integer id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else if (epicMap.containsKey(id)) {
            for (Integer sub : epicMap.get(id).getIdSubList()) {
                subMap.remove(sub);
            }
            epicMap.remove(id);
        } else if (subMap.containsKey(id)) {
            epicMap.get(subMap.get(id).getOwnEpic()).getIdSubList().remove(id);
            subMap.remove(id);
        }
        Manager.getDefaultHistory().remove(id);
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        subMap.clear();
        epicMap.clear();
        Manager.getDefaultHistory().clear();
    }

    @Override
    public Task getTask(Integer id) {
        if (taskMap.containsKey(id)) {
            Manager.getDefaultHistory().add(taskMap.get(id));
            return taskMap.get(id);
        } else if (epicMap.containsKey(id)) {
            Manager.getDefaultHistory().add(epicMap.get(id));
            return epicMap.get(id);
        } else if (subMap.containsKey(id)) {
            Manager.getDefaultHistory().add(subMap.get(id));
            return subMap.get(id);
        }
        return null;
    }

    @Override
    public List<Integer> getSubListFromEpic(Integer id) {
        return epicMap.get(id).getIdSubList();
    }

    @Override
    public List<Task> getHistory() {
        return Manager.getDefaultHistory().getHistory();
    }

    @Override
    public List<Task> getListAllTasks() {
        ArrayList<Task> listTasks = new ArrayList<>();
        {
            listTasks.addAll(taskMap.values());
            listTasks.addAll(epicMap.values());
            listTasks.addAll(subMap.values());
            listTasks.sort(Comparator.comparingInt(Task::getId));
        }
        return listTasks;
    }

    public static HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public static HashMap<Integer, EpicTask> getEpicMap() {
        return epicMap;
    }

    public static HashMap<Integer, SubTask> getSubMap() {
        return subMap;
    }

    public static int getCounterId() {
        return counterId;
    }

    public static void setCounterId(int counterId) {
        InMemoryTaskManager.counterId = counterId;
    }
}