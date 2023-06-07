package test;

import main.managers.*;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagersTest<HttpTaskManager> {
    KVServer kvServer = new KVServer();

    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        kvServer.start();
        manager = new HttpTaskManager("http://localhost:8078/");
    }

    @Test
    public void loadKVServer() throws Exception {
        Task task1 = new Task("httpTask", "descTask", 15, "2023-11-11 00:00");
        Task task2 = new Task("httpTask2", "descTask2", 15, "2023-11-12 00:00");
        Epic epic1 = new Epic("httpEpic1", "descEpic1");
        Epic epic2 = new Epic("httpEpic2", "descEpic2");
        Subtask subtask1 = new Subtask("httpSub1", "descSub1", 1);
        Subtask subtask2 = new Subtask("httpSub2", "descSub2", 1);

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        List<Task> tasks = manager.getListAllTasks();
        int expectedSize = 6;
        int actualSize = tasks.size();
        assertEquals(expectedSize, actualSize);
        assertEquals(expectedSize, manager.getCounterId());

        manager.getEpicMap().remove(epic1.getId());
        manager.getEpicMap().remove(epic2.getId());
        manager.getTaskMap().remove(task1.getId());
        manager.getTaskMap().remove(task2.getId());
        manager.getSubMap().remove(subtask1.getId());
        manager.getSubMap().remove(subtask2.getId());

        tasks = manager.getListAllTasks();
        expectedSize = 0;
        actualSize = tasks.size();
        assertEquals(expectedSize, actualSize);

        manager.loadFromKVS();

        tasks = manager.getListAllTasks();
        expectedSize = 6;
        actualSize = tasks.size();
        assertEquals(expectedSize, actualSize);
    }

    @AfterEach
    public void tearDown() throws Exception {
        kvServer.stop();
    }


}
