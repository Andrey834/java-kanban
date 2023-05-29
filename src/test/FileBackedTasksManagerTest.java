package test;

import main.managers.FileBackedTasksManager;
import main.managers.HistoryManager;
import main.managers.InMemoryHistoryManager;
import main.tasks.Epic;
import main.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    protected File  file = new File("resources", "databaseTest.csv");

    @BeforeEach
    public void setUp() {
        manager = new FileBackedTasksManager(file);
        manager.removeAllTasks();
    }

    @Test
    public void loadFromFileTest() {
        Task task1 = new Task(1, "newTask", "descNewTAsk");
        Task task2 = new Task(2, "newTask", "descNewTAsk");
        Task task3 = new Task(3, "newTask", "descNewTAsk");

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        final List<Task> tasksList = manager.getListAllTasks();

        assertEquals(3, tasksList.size());

        manager.getTaskMap().remove(task1.getId());
        manager.getTaskMap().remove(task2.getId());
        manager.getTaskMap().remove(task3.getId());

        final List<Task> tasksListAfterRemoveTasks = manager.getListAllTasks();

        assertEquals(0, tasksListAfterRemoveTasks.size());

        manager.loadFromFile();

        final List<Task> tasksListAfterLoad = manager.getListAllTasks();

        assertEquals(3, tasksListAfterLoad.size());

        Task taskLoad1 = manager.getTask(task1.getId());
        Task taskLoad2 = manager.getTask(task2.getId());
        Task taskLoad3 = manager.getTask(task3.getId());

        assertEquals(task1, taskLoad1, "Задача 1 отличается от загруженной");
        assertEquals(task2, taskLoad2, "Задача 2 отличается от загруженной");
        assertEquals(task3, taskLoad3, "Задача 3 отличается от загруженной");
    }

    @Test
    public void checkHistoryAfterLoad() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "newTask", "descNewTAsk");
        Task task2 = new Task(2, "newTask", "descNewTAsk");
        Task task3 = new Task(3, "newTask", "descNewTAsk");

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        final List<Task> tasksList = manager.getListAllTasks();
        assertEquals(3, tasksList.size());

        final List<Task> idListHistory = manager.getHistory();
        assertEquals(0, idListHistory.size());

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());

        final List<Task> idListHistoryAfterGetTasks = manager.getHistory();
        assertEquals(3, idListHistoryAfterGetTasks.size(), "Отличается размер истории с ожидаемым");

        manager.getTaskMap().remove(task1.getId());
        manager.getTaskMap().remove(task2.getId());
        manager.getTaskMap().remove(task3.getId());
        historyManager.clear();

        final List<Task> tasksListAfterRemoveTasks = manager.getListAllTasks();
        assertEquals(0, tasksListAfterRemoveTasks.size());

        final List<Task> idListHistoryAfterAfterClear = manager.getHistory();
        assertEquals(0, idListHistoryAfterAfterClear.size());

        manager.loadFromFile();

        final List<Task> idListHistoryAfterAfterLoad = manager.getHistory();
        assertEquals(3, idListHistoryAfterAfterLoad.size(), "Не загрузилась история просмотра Задач");
    }

    @Test
    public void loadEmptyData() {
        final List<Task> tasksList = manager.getListAllTasks();
        assertEquals(0, tasksList.size(), "loadEmptyData - присутствуют задачи");

        final List<Task> idListHistory = manager.getHistory();
        assertEquals(0, idListHistory.size(),"loadEmptyData - присутствуют история задач");

        final List<Task> idListPrioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(0, idListHistory.size(),"loadEmptyData - присутствуют приоритет задачи");

        manager.loadFromFile();

        final List<Task> tasksListAfterLoad = manager.getListAllTasks();
        assertEquals(0, tasksList.size(), "loadEmptyData - присутствуют задачи после пустой загрузке");

        final List<Task> idListHistoryAfterLoad = manager.getHistory();
        assertEquals(0, idListHistory.size(),"loadEmptyData - история задач после загрузки");

        final List<Task> idListPrioritizedTasksAfterLoad = manager.getPrioritizedTasks();
        assertEquals(0, idListHistory.size(),"loadEmptyData - приоритет задачи не пустой");;

    }

    @Test
    public void loadEmptyTasks() {
        final List<Task> tasksList = manager.getListAllTasks();
        assertEquals(0, tasksList.size(), "loadEmptyTasks - присутствуют задачи");

        manager.loadFromFile();

        final List<Task> tasksListAfterLoad = manager.getListAllTasks();
        assertEquals(0, tasksList.size(), "loadEmptyTasks - присутствуют задачи после пустой загрузке");
    }

    @Test
    public void loadEmptyEpic() {
        Epic epic = new Epic(1, "newTask", "descNewTAsk");
        manager.addNewEpic(epic);

        final List<Task> tasksList = manager.getListAllTasks();
        Epic savedEpic = manager.getEpic(epic.getId());

        assertEquals(1, tasksList.size(), "loadEmptyEpic - отсутствует Эпик-Задача");
        assertEquals(epic, savedEpic);

        manager.getEpicMap().remove(savedEpic.getId());

        final List<Task> tasksListAfterRemove = manager.getListAllTasks();
        assertEquals(0, tasksListAfterRemove.size(), "loadEmptyEpic - присутствует Эпик-Задача");

        manager.loadFromFile();

        Epic loadEpic = manager.getEpic(epic.getId());

        assertEquals(savedEpic, loadEpic, "loadEmptyEpic - Эпик-Задачи отличаются");
    }

    @Test
    public void loadEmptyHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "newTask", "descNewTAsk");
        Task task2 = new Task(2, "newTask", "descNewTAsk");
        Task task3 = new Task(3, "newTask", "descNewTAsk");

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        final List<Task> tasksList = manager.getListAllTasks();
        assertEquals(3, tasksList.size());

        final List<Task> idListHistory = manager.getHistory();
        assertEquals(0, idListHistory.size(), "loadEmptyHistory Размер истории не пуст");

        manager.getTaskMap().remove(task1.getId());
        manager.getTaskMap().remove(task2.getId());
        manager.getTaskMap().remove(task3.getId());

        final List<Task> tasksListAfterRemove = manager.getListAllTasks();
        assertEquals(0, tasksListAfterRemove.size());

        manager.loadFromFile();

        final List<Task> idListHistoryAfterLoad = manager.getHistory();
        assertEquals(0, idListHistoryAfterLoad.size(), "loadEmptyHistory Размер истории не пуст");

        final List<Task> tasksListAfterLoad = manager.getListAllTasks();
        assertEquals(3, tasksListAfterLoad.size());
    }
}

