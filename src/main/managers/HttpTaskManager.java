package main.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient client;
    private Gson gson = Managers.getGson();
    private final Type taskMapToken = new TypeToken<HashMap<Integer, Task>>() {}.getType();
    private final Type subMapToken = new TypeToken<HashMap<Integer, Subtask>>() {}.getType();
    private final Type epicMapToken = new TypeToken<HashMap<Integer, Epic>>() {}.getType();
    private final Type validMapToken = new TypeToken<HashMap<String, Boolean>>() {}.getType();
    private final Type tasksListToken = new TypeToken<List<Task>>() {}.getType();
    private final Type counterToken = new TypeToken<Integer>() {}.getType();

    public HttpTaskManager(String uri) throws IOException, InterruptedException {
        super();
        client = new KVTaskClient(uri);
        loadFromKVS();
    }

     public void loadFromKVS() {
        String tasks = client.load("tasks");
        String epics = client.load("epics");
        String subtasks = client.load("subtasks");
        String history = client.load("history");
        String priority = client.load("priority");
        String validation = client.load("validation");
        String counterId = client.load("counterId");

        if (!tasks.isBlank()) {
            setTaskMap(gson.fromJson(tasks, taskMapToken));
        }
        if (!epics.isBlank()) {
            setEpicMap(gson.fromJson(epics, epicMapToken));
        }
        if (!subtasks.isBlank()) {
            setSubMap(gson.fromJson(subtasks, subMapToken));
        }
        if (!history.isBlank()) {
            List<Integer> listHistory = gson.fromJson(history, tasksListToken);
            for (Integer task : listHistory) {
                if (getTaskMap().get(task) != null) {
                    Managers.getDefaultHistory().add(getTaskMap().get(task));
                } else if (getEpicMap().get(task) != null) {
                    Managers.getDefaultHistory().add(getEpicMap().get(task));
                } else if (getSubMap().get(task) != null) {
                    Managers.getDefaultHistory().add(getSubMap().get(task));
                }
            }
        }
        if (!priority.isBlank()) {
            setPrioritizedTasks(gson.fromJson(priority, tasksListToken));
        }
        if (!validation.isBlank()) {
            setValidationDate(gson.fromJson(validation, validMapToken));
        }
        if (!counterId.isBlank()) {
            setCounterId(gson.fromJson(counterId, counterToken));
        }
    }

    public void saveToKVS() {
        String tasks = gson.toJson(getTaskMap());
        String epics = gson.toJson(getEpicMap());
        String subtasks = gson.toJson(getSubMap());
        String history = gson.toJson(getHistory());
        String priority = gson.toJson(getPrioritizedTasks());
        String validation = gson.toJson(getValidationDate());
        String counterId = gson.toJson(getCounterId());

        client.put("tasks", tasks);
        client.put("epics", epics);
        client.put("subtasks", subtasks);
        client.put("history", history);
        client.put("priority", priority);
        client.put("validation", validation);
        client.put("counterId", counterId);
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        saveToKVS();
    }

    @Override
    public void addNewEpic(Epic epicTask) {
        super.addNewEpic(epicTask);
        saveToKVS();
    }

    @Override
    public void addNewSubtask(Subtask subTask) {
        super.addNewSubtask(subTask);
        saveToKVS();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        saveToKVS();
    }

    @Override
    public void updateEpic(Epic epicTask) {
        super.updateEpic(epicTask);
        saveToKVS();
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);
        saveToKVS();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        saveToKVS();
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        saveToKVS();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        saveToKVS();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        saveToKVS();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        saveToKVS();
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        saveToKVS();
        return epic;

    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        saveToKVS();
        return subtask;

    }
}
