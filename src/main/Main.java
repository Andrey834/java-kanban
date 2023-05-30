package main;


import main.managers.FileBackedTasksManager;
import main.managers.InMemoryTaskManager;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("resources", "database.csv"));
        manager.loadFromFile();

        manager.addNewTask(new Task("testTask", "descTask", 30, "2023-11-27 15:15"));
        manager.addNewEpic(new Epic("testEpic", "descEpic"));
        manager.addNewSubtask(new Subtask("testSub12", "descSub1", 15, "2023-05-26 15:45", 2));
        manager.addNewSubtask(new Subtask("testSub2", "descSub2", 45, "2023-05-27 15:15", 2));
        manager.addNewSubtask(new Subtask("testSub3", "descSub3", 75, "2023-02-27 19:15",2));

        //список приоритета задач
        manager.printTableTasks(manager.getPrioritizedTasks());

        manager.updateSubtask(new Subtask(3, StatusTask.DONE));
        manager.updateSubtask(new Subtask(4, StatusTask.DONE));
        manager.updateSubtask(new Subtask(5, StatusTask.DONE));

        manager.getTask(1);
        manager.getSubtask(3);
        manager.getSubtask(4);
        manager.getEpic(2);

        manager.printTableTasks(manager.getHistory());

        manager.printTableTasks(manager.getListAllTasks());

        manager.printTableTasks(manager.getPrioritizedTasks());
    }
}