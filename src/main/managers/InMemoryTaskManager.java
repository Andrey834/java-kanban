package main.managers;

import main.tasks.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, Subtask> subMap = new HashMap<>();
    private int counterId = 0;
    private HashMap<String, Boolean> validationDate = new HashMap<>(35040);
    private List<Task> prioritizedTasks = new ArrayList<>();
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public void addNewTask(Task task) {
        fillingIntervalValidationsDateAndTime();
        if (validateFreeTime(task)) {
            int id = ++counterId;
            task.setId(id);
            taskMap.put(id, task);
            prioritizedTasks.add(task);
            addTimeInValidationDate(task);
        }
    }

    @Override
    public void addNewEpic(Epic epicTask) {
        fillingIntervalValidationsDateAndTime();
        int id = ++counterId;
        epicTask.setId(id);
        epicMap.put(id, epicTask);
    }

    @Override
    public void addNewSubtask(Subtask subTask) {
        if (epicMap.containsKey(subTask.getOwnEpic()) && validateFreeTime(subTask)) {
            int id = ++counterId;
            int idOwnEpic = subTask.getOwnEpic();
            subTask.setId(id);
            subTask.addIdSubtaskInEpicList(epicMap.get(idOwnEpic));
            subTask.whenAddSubChangeStartTimeAndDurationEpic(epicMap.get(idOwnEpic));
            epicMap.get(idOwnEpic).setEndTime(subTask);
            subMap.put(id, subTask);
            prioritizedTasks.add(subTask);
            addTimeInValidationDate(subTask);
        }
    }

    @Override
    public void updateTask(Task task) {
        fillingIntervalValidationsDateAndTime();
        if (checkContainsTaskInMap(task)) {
            Task oldTask = taskMap.get(task.getId());
            task.substitutionOfOldValuesOnUpdate(oldTask);
            removeOldTimeWhenChangeTask(oldTask);
            if(validateFreeTime(task)) {
                taskMap.put(task.getId(), task);
                historyManager.updateNode(task);
                addTimeInValidationDate(task);
                prioritizedTasks.remove(oldTask);
                prioritizedTasks.add(task);
            } else {
                addTimeInValidationDate(oldTask);
            }
        }
        if (task.getStatus().equals(StatusTask.DONE)) {
            removeOldTimeWhenChangeTask(task);
        }
    }

    @Override
    public void updateEpic(Epic epicTask) {
        fillingIntervalValidationsDateAndTime();
        if (checkContainsTaskInMap(epicTask)) {
            Epic oldEpic = epicMap.get(epicTask.getId());
            epicTask.substitutionOfOldValuesOnUpdate(oldEpic);
            epicMap.put(epicTask.getId(), epicTask);
            historyManager.updateNode(epicTask);
        }
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        fillingIntervalValidationsDateAndTime();
        if (checkContainsTaskInMap(subTask)) {
            Subtask oldSubtask = subMap.get(subTask.getId());
            subTask.substitutionOfOldValuesOnUpdate(oldSubtask);
            removeOldTimeWhenChangeTask(oldSubtask);
            if (validateFreeTime(subTask)) {
                int idOwnEpic = oldSubtask.getOwnEpic();
                subTask.whenUpdateSubChangeStartTimeAndDurationEpic(epicMap.get(idOwnEpic), oldSubtask);
                epicMap.get(idOwnEpic).whenUpdateSubtaskUpdateEndTimeEpic(oldSubtask, subTask);
                subMap.put(subTask.getId(), subTask);
                epicMap.get(idOwnEpic).checkStatusSubFromEpic(subMap);
                addTimeInValidationDate(subTask);
                historyManager.updateNode(subTask);
                historyManager.updateNode(epicMap.get(idOwnEpic));
                prioritizedTasks.remove(oldSubtask);
                prioritizedTasks.add(subTask);
            } else {
                addTimeInValidationDate(oldSubtask);
            }
            if (subTask.getStatus().equals(StatusTask.DONE)) {
                removeOldTimeWhenChangeTask(subTask);
            }
        }
    }

    @Override
    public void removeTask(Integer id) {
        if (taskMap.get(id) != null) {
            historyManager.remove(id);
            removeOldTimeWhenChangeTask(taskMap.get(id));
            prioritizedTasks.remove(taskMap.get(id));
            taskMap.remove(id);
            System.out.println("Задача с ID-" + id + " удалена!");
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + id);
        }
    }

    @Override
    public void removeEpic(Integer id) {
        if (epicMap.get(id) != null) {
            final List<Integer> idSubFromEpic = getEpicMap().get(id).getIdListSubtasks();
            for (Integer integer : idSubFromEpic) {
                prioritizedTasks.remove(subMap.get(integer));
                removeOldTimeWhenChangeTask(subMap.get(integer));
                getSubMap().remove(integer);
            }
            historyManager.remove(id);
            getEpicMap().remove(id);
            System.out.println("Эпик-Задача с ID-" + id + " удалена!");
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + id);
        }
    }

    @Override
    public void removeSubtask(Integer id) {
        if (subMap.get(id) != null) {
            historyManager.remove(id);
            Subtask removeSubtask = subMap.get(id);
            removeSubtask.removeIdSubtaskInEpicList(epicMap.get(removeSubtask.getOwnEpic()));
            prioritizedTasks.remove(subMap.get(id));
            removeOldTimeWhenChangeTask(subMap.get(id));
            subMap.remove(id);
            System.out.println("Суб-Задача с ID-" + id + " удалена!");
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + id);
        }
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        subMap.clear();
        epicMap.clear();
        historyManager.clear();
        validationDate.clear();
        prioritizedTasks.clear();
    }

    @Override
    public Task getTask(Integer id) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            historyManager.add(task);
            return task;
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + id);
            return null;
        }
    }

    @Override
    public Epic getEpic(Integer id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + id);
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Integer id) {
        if (subMap.containsKey(id)) {
            Subtask subtask = subMap.get(id);
            historyManager.add(subtask);
            return subtask;
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + id);
            return null;
        }
    }

    @Override
    public List<Integer> getSubListFromEpic(Integer id) {
        return epicMap.get(id).getIdListSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return Managers.getDefaultHistory().getHistory();
    }

    @Override
    public List<Task> getListAllTasks() {
        ArrayList<Task> listTasks = new ArrayList<>();
        listTasks.addAll(taskMap.values());
        listTasks.addAll(epicMap.values());
        listTasks.addAll(subMap.values());
        listTasks.sort(Comparator.comparingInt(Task::getId));
        return listTasks;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        prioritizedTasks.sort(Comparator.comparing(Task::getStartTime));
        return prioritizedTasks;
    }

    public List<String> getValidListDate() {
        List<String> listBusyTime = new ArrayList<>();
        for (String s : validationDate.keySet()) {
            if (!validationDate.get(s)) {
                listBusyTime.add(s);
            }
        }
        return listBusyTime;
    }

    public void printTableTasks(List<Task> tasksList) {
        System.out.printf(" %4s | %-9s | %-15s | %-12S | %-20s | %-4s | %-16S | %-16S",
                "ID", "TYPE", "TITLE", "STATUS", "DESCRIPTION", "TIME", "StartTime", "EndTime");
        tasksList.forEach(this::printTasks);
        System.out.println("\n");
    }

    public void printTasks(Task task) {
        System.out.printf("\n %4d | %-9s | %-15s | %-12S | %-20s | %-4d | %16S | %16S",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task.getDuration(),
                task.getStartTime(),
                task.getEndTime());
    }

    private boolean checkContainsTaskInMap(Task task) {
        boolean found = false;
        if (task.getType().equals(TypeTask.TASK) && taskMap.containsKey(task.getId())) {
            found = true;
        } else if (task.getType().equals(TypeTask.SUBTASK) && subMap.containsKey(task.getId())) {
            found = true;
        } else if (task.getType().equals(TypeTask.EPIC) && epicMap.containsKey(task.getId())) {
            found = true;
        } else {
            System.out.println("Отсутствует задача с указанным ID-" + task.getId() + " Type-" + task.getType());
        }
        return found;
    }

    protected void fillingIntervalValidationsDateAndTime() {
        if (validationDate.isEmpty()) {
            int INTERVAL_15MIN_IN_YEAR = 365 * 24 * 4;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime timeStarter = LocalDateTime.of(2023, 1, 1, 0, 0);
            for (int i = 0; i < INTERVAL_15MIN_IN_YEAR; i++) {
                validationDate.put(timeStarter.format(formatter), true);
                timeStarter = timeStarter.plusMinutes(15);
            }
        }
    }

    public List<String> getIntervalTimeFromTask(Task task) {
        List<String> intervalsTimeTask = new ArrayList<>();
        if (task.getStartTime() != null) {
            LocalDateTime checkTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();
            int INTERVAL = 15;

            while (!checkTime.equals(endTime)) {
                intervalsTimeTask.add(checkTime.format(task.getFormatter()));
                checkTime = checkTime.plusMinutes(INTERVAL);
            }
        }
        return intervalsTimeTask;
    }

    protected boolean validateFreeTime(Task task) {
        boolean valid = true;
        if (task.getStartTime() != null) {
            List<String> intervalsTimeTask = getIntervalTimeFromTask(task);
            List<String> busyListTime = new ArrayList<>();

            for (String interval : intervalsTimeTask) {
                if (validationDate.get(interval) != null && !validationDate.get(interval)) {
                    busyListTime.add(interval);
                    valid = false;
                }
            }

            if (!valid) {
                System.out.println("Указанное время выполнения занято:");
                busyListTime.stream().sorted().forEach(System.out::println);
            }
        }
        return valid;
    }

    protected void addTimeInValidationDate(Task task) {
        if (task.getStartTime() != null) {
            List<String> intervalsTimeTask = getIntervalTimeFromTask(task);
            for (String time : intervalsTimeTask) {
                validationDate.put(time, false);
            }
        }
    }

    protected void removeOldTimeWhenChangeTask(Task oldTask) {
        List<String> removeTime = getIntervalTimeFromTask(oldTask);
        removeTime.forEach(validationDate::remove);
    }

    @Override
    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubMap() {
        return subMap;
    }

    public void setTaskMap(HashMap<Integer, Task> taskMap) {
        this.taskMap = taskMap;
    }

    public void setEpicMap(HashMap<Integer, Epic> epicMap) {
        this.epicMap = epicMap;
    }

    public void setSubMap(HashMap<Integer, Subtask> subMap) {
        this.subMap = subMap;
    }

    public void setPrioritizedTasks(List<Task> prioritizedTasks) {
        this.prioritizedTasks = prioritizedTasks;
    }

    public void setValidationDate(HashMap<String, Boolean> validationDate) {
        this.validationDate = validationDate;
    }

    public HashMap<String, Boolean> getValidationDate() {
        return validationDate;
    }

    public int getCounterId() {
        return counterId;
    }

    public void setCounterId(int counterId) {
        this.counterId = counterId;
    }
}
