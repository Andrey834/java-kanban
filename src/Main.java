import manager.Managers;
import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

        taskManager.saveTask(new Task("Простая 1", "Сходить в магазин 1"));
        taskManager.saveTask(new Task("Простая 2", "Сходить в магазин 2"));
        taskManager.saveTask(new Task("Простая 3", "Сходить в магазин 3"));

        taskManager.updateTask(new Task(1, "Простая 1 new", "Сходить в магазин 1 new",
                StatusTask.IN_PROGRESS));
        taskManager.updateTask(new Task(2, "Простая 2 new", "Сходить в магазин 2 new",
                StatusTask.IN_PROGRESS));
        taskManager.updateTask(new Task(3, "Простая 3 new", "Сходить в магазин 3 new",
                StatusTask.IN_PROGRESS));

        taskManager.saveEpicTask(new EpicTask("Эпик 1", "Переезд 1"));
        taskManager.saveEpicTask(new EpicTask("Эпик 1", "Переезд 2"));
        taskManager.saveEpicTask(new EpicTask("Эпик 1", "Переезд 3"));

        taskManager.updateEpicTask(new EpicTask(4, "Эпик 1 new", "Переезд 1 new"));
        taskManager.updateEpicTask(new EpicTask(5, "Эпик 2 new", "Переезд 2 new"));
        taskManager.updateEpicTask(new EpicTask(6, "Эпик 3 new", "Переезд 2 new"));

        taskManager.saveSubTask(new SubTask("субЭпик 1", "ds 4-1", 4));
        taskManager.saveSubTask(new SubTask("субЭпик 2", "ds 4-2", 4));
        taskManager.saveSubTask(new SubTask("субЭпик 3", "ds 5-1", 5));
        taskManager.saveSubTask(new SubTask("субЭпик 4", "ds 5-2", 5));
        taskManager.saveSubTask(new SubTask("субЭпик 5", "ds 6-1", 6));
        taskManager.saveSubTask(new SubTask("субЭпик 6", "ds 6-2", 6));
        taskManager.saveSubTask(new SubTask("субЭпик 7", "ds 6-3", 6));

        taskManager.getSubListFromEpic(4);

        taskManager.updateSubTask(new SubTask(7, "субЭпик 1 new", "ds 4-1", StatusTask.NEW));
        taskManager.updateSubTask(new SubTask(8, "субЭпик 2 new", "ds 4-2", StatusTask.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask(9, "субЭпик 3 new", "ds 5-1", StatusTask.DONE));
        taskManager.updateSubTask(new SubTask(10, "субЭпик 4 new", "ds 5-2", StatusTask.DONE));
        taskManager.updateSubTask(new SubTask(11, "субЭпик 5 new", "ds 6-1", StatusTask.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask(12, "субЭпик 6 new", "ds 6-2", StatusTask.IN_PROGRESS));

        taskManager.removeTask(2);

        System.out.println("-------------------------------");
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);
        taskManager.getTask(4);
        taskManager.getTask(5);
        taskManager.getTask(6);
        taskManager.getTask(7);
        taskManager.getTask(8);
        taskManager.getTask(9);
        taskManager.getTask(10);
        taskManager.getTask(11);
        taskManager.getTask(12);
        taskManager.getTask(7);
        System.out.println("-------------------------------");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("-------------------------------");
        taskManager.removeAllTasks();
        System.err.println("Вопрос! При удаление всех задач, историю просмотра чистим?");

    }
}