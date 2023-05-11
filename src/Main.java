import managers.FileBackedTasksManager;
import tasks.EpicTask;
import tasks.StatusTask;
import tasks.SubTask;
import tasks.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("resources", "database.csv"));

        manager.loadFromFile();
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("---------");

        manager.createTask(new Task("lastTask", "descLast"));
        manager.createEpicTask(new EpicTask("epicTask", "descEpic2"));
        manager.createSubTask(new SubTask("subTask1", "descSub", 2));
        manager.createSubTask(new SubTask("subTask2", "descSub", 2));
        manager.updateSubTask(new SubTask(3 ,"lastTask", "descLast", StatusTask.DONE));

        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        manager.getTask(1);
        manager.getTask(4);
        manager.createSubTask(new SubTask("subTask3", "descSub", 2));

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}