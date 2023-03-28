import manager.Manager;
import tasks.EpicTask;
import tasks.SimpleTask;
import tasks.StatusTask;
import tasks.SubTask;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.saveSimpleTask(new SimpleTask("Простая задача", "Выкинуть мусор"));
        manager.saveSimpleTask(new SimpleTask("Простая задача 2", "Выкинуть мусор 2"));

        manager.saveEpicTask(new EpicTask("Эпик задача", "Поездка на дачу"));
        manager.saveEpicTask(new EpicTask("Эпик задача 2", "Организация праздника"));


        manager.saveSubTask(new SubTask("Суб задача", "Заправить авто", 3));
        manager.saveSubTask(new SubTask("Суб задача", "Собрать вещи", 3));
        manager.saveSubTask(new SubTask("Суб задача", "Запланировать маршрут", 3));

        manager.saveSubTask(new SubTask("Суб задача2", "Купить продукты", 4));
        manager.saveSubTask(new SubTask("Суб задача2", "Купить алкоголь", 4));
        manager.saveSubTask(new SubTask("Суб задача2", "Купить еще алкоголя", 4));

        manager.removeTask(10);

        manager.updateSimpleTask(new SimpleTask(2, "Простая задача 2", "Не выкидывать мусор",
                StatusTask.IN_PROGRESS));
        manager.updateEpicTask(new EpicTask(4, "Эпик задача 3 new", "Организация детского праздника"));

        //manager.removeAllTasks();


        manager.updateSubTask(new SubTask(8, "Суб задача2 new", "Купить продукты",
                StatusTask.DONE));
        manager.updateSubTask(new SubTask(9, "Суб задача2 new", "Купить алкоголь",
                StatusTask.IN_PROGRESS));
        manager.updateSubTask(new SubTask(10, "Суб задача2 new", "Купить еще алкоголя",
                StatusTask.DONE));

        //manager.getTask(4);
        manager.getSubListFromEpic(4);







    }
}