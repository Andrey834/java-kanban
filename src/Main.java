import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

        taskManager.saveEpicTask(new EpicTask("Эпик 1", "Переезд 1"));
        taskManager.saveEpicTask(new EpicTask("Эпик 2", "Переезд 2"));
        taskManager.saveEpicTask(new EpicTask("Эпик 3", "Переезд 3"));

        taskManager.saveSubTask(new SubTask("субЭпик 1", "ds 1-1", 1));
        taskManager.saveSubTask(new SubTask("субЭпик 2", "ds 1-2", 1));
        taskManager.saveSubTask(new SubTask("субЭпик 3", "ds 1-3", 1));
        taskManager.saveSubTask(new SubTask("субЭпик 4", "ds 2-1", 2));
        taskManager.saveSubTask(new SubTask("субЭпик 5", "ds 2-2", 2));
        taskManager.saveSubTask(new SubTask("субЭпик 6", "ds 2-3", 2));

        taskManager.getTask(9);
        taskManager.getTask(7);
        taskManager.getTask(5);
        taskManager.getTask(3);
        taskManager.getTask(1);
        taskManager.getTask(8);
        taskManager.getTask(6);
        taskManager.getTask(4);
        taskManager.getTask(2);

        System.out.println("----------------------");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("----------------------");

        taskManager.getTask(9);
        taskManager.getTask(7);
        taskManager.getTask(8);

        System.out.println("----------------------");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeTask(1); //Удаляем эпик №1 + его СубЗадачи

        System.out.println("----------------------");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeAllTasks(); //Удаляем все задачи + историю просмотра

        System.out.println("----------------------");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("ПУСТО!");
    }
}