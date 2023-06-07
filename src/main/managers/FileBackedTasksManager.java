package main.managers;

import main.tasks.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager() {
        this.file = new File("resources", "database.csv");
        File dir = new File(file.getParent());
        try {
            if (!dir.isDirectory()) {
                Files.createDirectory(Path.of(file.getParent()));
            }
            if (!file.exists()) {
                Files.createFile(Path.of(file.toURI()));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("File creation error!");
        }
    }

    private void save() {
        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);) {
            writer.println("id,type,title,status,description,duration,startTime,epic");
            getListAllTasks().stream().map(Task::toString).forEach(writer::println);
            writer.println();
            writer.print(historyToString(Managers.getDefaultHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Error writing to file!");
        }
    }

    public void loadFromFile() {
        try {
            List<String> taskLine = Files.readAllLines(file.toPath());
            int counterLoadId = 0;
            if (taskLine.size() > 0) {
                for (int i = 1; i < taskLine.size(); i++) {
                    if (!taskLine.get(i).isEmpty()) {
                        TypeTask type = Objects.requireNonNull(fromString(taskLine.get(i))).getType();
                        switch (type) {
                            case TASK -> {
                                Task task = fromString(taskLine.get(i));
                                getPrioritizedTasks().add(task);
                                fillingIntervalValidationsDateAndTime();
                                getTaskMap().put(task.getId(), task);
                                addTimeInValidationDate(task);
                                counterLoadId = counterLoadId + 1;
                            }
                            case EPIC -> {
                                Epic epicTask = (Epic) fromString(taskLine.get(i));
                                fillingIntervalValidationsDateAndTime();
                                getEpicMap().put(epicTask.getId(), epicTask);
                                counterLoadId = counterLoadId + 1;
                            }
                            case SUBTASK -> {
                                Subtask subTask = (Subtask) fromString(taskLine.get(i));
                                fillingIntervalValidationsDateAndTime();
                                getSubMap().put(subTask.getId(), subTask);
                                getPrioritizedTasks().add(subTask);
                                subTask.addIdSubtaskInEpicList(getEpicMap().get(subTask.getOwnEpic()));
                                getEpicMap().get(subTask.getOwnEpic()).setEndTime(subTask);
                                addTimeInValidationDate(subTask);
                                counterLoadId = counterLoadId + 1;
                            }
                        }
                    } else {
                        break;
                    }
                }
                setCounterId(counterLoadId);
                List<Integer> historyId = new ArrayList<>(historyFromString(taskLine.get(taskLine.size() - 1)));
                for (Integer idTask : historyId) {
                    if (getTaskMap().get(idTask) != null) {
                        Managers.getDefaultHistory().add(getTaskMap().get(idTask));
                    } else if (getEpicMap().get(idTask) != null) {
                        Managers.getDefaultHistory().add(getEpicMap().get(idTask));
                    } else if (getSubMap().get(idTask) != null) {
                        Managers.getDefaultHistory().add(getSubMap().get(idTask));
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Error loading from file!");
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TypeTask typeTask = TypeTask.valueOf(parts[1]);
        String title = parts[2];
        StatusTask status = StatusTask.valueOf(parts[3]);
        String description = parts[4];
        int duration = Integer.parseInt(parts[5]);
        LocalDateTime startTime = LocalDateTime.parse(parts[6]);
        switch (typeTask) {
            case TASK -> {
                return new Task(id, TypeTask.TASK , title, description, status, duration, startTime);
            }
            case EPIC -> {
                return new Epic(id,TypeTask.EPIC ,title, description, status, duration, startTime, new ArrayList<>());
            }
            case SUBTASK -> {
                int ownEpic = Integer.parseInt(parts[7]);
                return new Subtask(id, TypeTask.SUBTASK ,title, description, status, duration, startTime, ownEpic);
            }
            default -> {
                return null;
            }
        }
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder idHistory = new StringBuilder();
        for (Task task : manager.getHistory()) {
            idHistory.append(task.getId()).append(",");
        }
        return idHistory.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyList = new ArrayList<>();
        if (value.length() > 0) {
            String[] idHistory = value.split(",");
            Arrays.stream(idHistory).toList().stream().map(Integer::parseInt).forEach(historyList::add);
        }
        return historyList;
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        return epic;

    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        return subtask;
    }
}
