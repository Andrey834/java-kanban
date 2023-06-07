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

public class FileBackedTasksManagersTest extends TaskManagersTest<FileBackedTasksManager> {
    protected File  file = new File("resources", "databaseTest.csv");

    @BeforeEach
    public void setUp() {
        manager = new FileBackedTasksManager();
        manager.removeAllTasks();
    }

    @Test
    public void loadEmptyData() {
        final List<Task> actualTasksList = manager.getListAllTasks();
        assertEquals(0, actualTasksList.size(), "loadEmptyData - присутствуют задачи");

        final List<Task> actualIdListHistory = manager.getHistory();
        assertEquals(0, actualIdListHistory.size(),"loadEmptyData - присутствуют история задач");

        final List<Task> actualIdListPrioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(0, actualIdListPrioritizedTasks.size(),"loadEmptyData - присутствуют приоритет задачи");

        manager.loadFromFile();

        final List<Task> actualTasksListAfterLoad = manager.getListAllTasks();
        assertEquals(0, actualTasksListAfterLoad.size(), "loadEmptyData - присутствуют задачи после пустой загрузке");

        final List<Task> actualIdListHistoryAfterLoad = manager.getHistory();
        assertEquals(0, actualIdListHistoryAfterLoad.size(),"loadEmptyData - история задач после загрузки");

        final List<Task> actualIdListPrioritizedTasksAfterLoad = manager.getPrioritizedTasks();
        assertEquals(0, actualIdListPrioritizedTasksAfterLoad.size(),"loadEmptyData - приоритет задачи не пустой");;

    }

    @Test
    public void loadEmptyTasks() {
        final List<Task> actualTasksList = manager.getListAllTasks();
        assertEquals(0, actualTasksList.size(), "loadEmptyTasks - присутствуют задачи");

        manager.loadFromFile();

        final List<Task> actualTasksListAfterLoad = manager.getListAllTasks();
        assertEquals(0, actualTasksListAfterLoad.size(), "loadEmptyTasks - присутствуют задачи после пустой загрузке");
    }
}

