package test;

import main.managers.InMemoryTaskManager;
import main.tasks.Task;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void getValidationDateTest() {
        manager.addNewTask(new Task("simpleTask1", "descTask", 15, "2023-05-25 12:00"));
        manager.addNewTask(new Task("simpleTask2", "descTask", 15, "2023-05-25 12:15"));
        manager.addNewTask(new Task("simpleTask3", "descTask", 15, "2023-05-25 12:30"));

        Task task1 = manager.getTask(1);
        Task task2 = manager.getTask(2);
        Task task3 = manager.getTask(3);

        assertNotNull(task1, "Задача 1 не найдена");
        assertNotNull(task2, "Задача 2 не найдена");
        assertNotNull(task3, "Задача 3 не найдена");

        final List<String> actualListTime = manager.getValidationDate();
        final List<String> expectedListTime = List.of(
                task2.getStartTime().format(task1.getFormatter()),
                task1.getStartTime().format(task2.getFormatter()),
                task3.getStartTime().format(task3.getFormatter())
        );
        assertEquals(expectedListTime, actualListTime);
    }

    @Test
    void getIntervalTimeFromTaskTest() {
        manager.addNewTask(new Task("simpleTask1", "descTask", 30, "2023-05-25 12:00"));

        Task task = manager.getTask(1);

        assertNotNull(task, "Задача не найдена");

        final List<String> actualListTime = manager.getIntervalTimeFromTask(task);
        final List<String> expectedListTime = List.of(
                task.getStartTime().format(task.getFormatter()),
                task.getStartTime().plusMinutes(15).format(task.getFormatter())
        );
        assertEquals(expectedListTime, actualListTime);
    }

    @Test
    void whenAddNewTaskCounterIdAdd1() {
        assertEquals(0, manager.getCounterId());
        manager.addNewTask(new Task("simpleTask1", "descTask", 30, "2023-05-25 12:00"));
        assertEquals(1, manager.getCounterId());
        manager.addNewTask(new Task("simpleTask1", "descTask", 30, "2023-05-28 12:00"));
        assertEquals(2, manager.getCounterId());
    }

    @AfterEach
    public void AfterEach() {
        manager.removeAllTasks();
    }
}
