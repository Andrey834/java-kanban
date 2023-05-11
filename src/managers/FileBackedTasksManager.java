package managers;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
        File dir = new File(file.getParent());
        try {
            if (!dir.isDirectory()) {
                Files.createDirectory(Path.of(file.getParent()));
            }
            if (!file.exists()) {
                Files.createFile(Path.of(file.toURI()));
            }
        }catch (IOException exception) {
            throw new ManagerSaveException("File creation error!");
        }
    }

    private void save() {
        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);) {
            writer.println("id,type,name,status,description,epic");
            for (Task task : getListAllTasks()) {
                writer.println(task.toString());
            }
            writer.print("\n");
            writer.print(historyToString(Manager.getDefaultHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Error writing to file");
        }
    }

    public void loadFromFile() {
        try {
            List<String> taskLine = Files.readAllLines(file.toPath());
            if (taskLine.size() > 1) {
                for (int i = 1; i < taskLine.size(); i++) {
                    if (!taskLine.get(i).isEmpty()) {
                        TypeTask typeTask = TypeTask.valueOf(
                                fromString(taskLine.get(i)).getClass().getSimpleName().toUpperCase()
                        );
                        switch (typeTask) {
                            case TASK -> {
                                Task task = fromString(taskLine.get(i));
                                getTaskMap().put(task.getId(), task);
                                setCounterId(getCounterId() + 1);
                            }
                            case EPICTASK -> {
                                EpicTask epicTask = (EpicTask) fromString(taskLine.get(i));
                                getEpicMap().put(epicTask.getId(), epicTask);
                                setCounterId(getCounterId() + 1);
                            }
                            case SUBTASK -> {
                                SubTask subTask = (SubTask) fromString(taskLine.get(i));
                                getSubMap().put(subTask.getId(), subTask);
                                setCounterId(getCounterId() + 1);
                                getEpicMap().get(subTask.getOwnEpic()).getIdSubList().add(subTask.getId());
                            }
                        }
                    } else {
                        break;
                    }
                }
                int lineHistory = taskLine.size() - 1;
                for (Integer integer : historyFromString(taskLine.get(lineHistory))) {
                    Manager.getDefault().getTask(integer);
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Error loading from file!");
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String title = parts[2];
        StatusTask status = StatusTask.valueOf(parts[3]);
        String desc = parts[4];
        TypeTask typeTask = TypeTask.valueOf(parts[1].toUpperCase());
        switch (typeTask) {
            case TASK -> {
                return new Task(id, title, desc, status);
            }
            case EPICTASK -> {
                return new EpicTask(id, title, desc, status, new ArrayList<>());
            }
            case SUBTASK -> {
                int ownEpic = Integer.parseInt(parts[5]);
                return new SubTask(id, title, desc, status, ownEpic);
            }
            default -> {
                return null;
            }
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder idHistory = new StringBuilder();
        for (Task task : manager.getHistory()) {
            idHistory.append(task.getId()).append(",");
        }
        return idHistory.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] idHistory = value.split(",");
        List<Integer> idListForHistory = new ArrayList<>();
        for (String sub : idHistory) {
            idListForHistory.add(Integer.parseInt(sub));
        }
        return  idListForHistory;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        super.createEpicTask(epicTask);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }
}
