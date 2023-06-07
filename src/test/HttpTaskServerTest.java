package test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.managers.*;
import main.tasks.StatusTask;
import main.tasks.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager taskManager;
    private HttpClient client;
    private KVServer kvServer;
    private HttpTaskServer server;
    private Gson gson;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void setUp() throws Exception {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
        task1 = new Task("httpTask", "descTask", 15, "2023-11-11 00:00");
        task2 = new Task("httpTask2", "descTask2", 15, "2023-11-12 00:00");
        epic1 = new Epic("httpEpic1", "descEpic1");
        epic2 = new Epic("httpEpic2", "descEpic2");
        subtask1 = new Subtask("httpSub1", "descSub1", 1);
        subtask2 = new Subtask("httpSub2", "descSub2", 1);
        taskManager = server.getTaskManager();
    }

    @Test
    void addNewTaskTest() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST addNewTask");

        HashMap<Integer, Task> actualMap = taskManager.getTaskMap();
        int expectedTaskMapSize = 1;
        assertNotNull(actualMap, "Пустая таблица с задачами");
        assertEquals(expectedTaskMapSize, actualMap.size(), "Отличается размер таблицы addNewTask POST");

        task1.setId(1);
        Task expectedTask = task1;
        Task actualTask = taskManager.getTaskMap().get(1);
        assertEquals(expectedTask, actualTask, "Задачи различаются: addNewTask POST");
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        Task actualTask = taskManager.getTaskMap().get(1);
        Task updateTask = new Task(
                1
                , TypeTask.TASK
                , "updateTask"
                , "updateDesc"
                , StatusTask.IN_PROGRESS
                , 30
                , LocalDateTime.of(2023, 2, 1, 0, 0)
        );
        assertNotEquals(updateTask, actualTask);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateTask)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST updateTaskTest");

        Task actualUpdateTask = taskManager.getTaskMap().get(1);
        assertEquals(updateTask, actualUpdateTask);
    }

    @Test
    void getTaskTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        HashMap<Integer, Task> actualMap = taskManager.getTaskMap();
        assertTrue(actualMap.containsValue(task1));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/task?id=" + task1.getId()))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST getTaskTest");

        Task actualTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task1, actualTask, "Задачи различаются: GET getTaskTest");
    }

    @Test
    void getTasksListTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        HashMap<Integer, Task> actualMap = taskManager.getTaskMap();
        int expectedSizeMap = 2;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: getTasksListTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 GET getTasksListTest");

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actualTasksList = gson.fromJson(response.body(), taskType);
        assertTrue(actualTasksList.contains(task1));
        assertTrue(actualTasksList.contains(task2));
        assertEquals(expectedSizeMap, actualTasksList.size());
    }

    @Test
    void removeTaskTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        HashMap<Integer, Task> actualMap = taskManager.getTaskMap();
        int expectedSizeMap = 1;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeTaskTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/task?id=" + task1.getId()))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 DELETE removeTaskTest");

        actualMap = taskManager.getTaskMap();
        expectedSizeMap = 0;
        actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeTaskTest");
    }

    @Test
    void removeTasksTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        HashMap<Integer, Task> actualMap = taskManager.getTaskMap();
        int expectedSizeMap = 2;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeTasksTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 DELETE removeTasksTest");

        actualMap = taskManager.getTaskMap();
        expectedSizeMap = 0;
        actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeTasksTest");
    }

    @Test
    void addNewEpicTest() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 addNewEpic POST");

        HashMap<Integer, Epic> actualMap = taskManager.getEpicMap();
        int expectedEpicMapSize = 1;
        assertEquals(expectedEpicMapSize, actualMap.size(), "Отличается размер таблицы addNewEpic POST");

        epic1.setId(1);
        Epic expectedEpic = epic1;
        Epic actualEpic = taskManager.getEpicMap().get(1);
        assertEquals(expectedEpic, actualEpic, "Эпик-Задачи различаются: addNewEpic POST");
    }

    @Test
    void updateEpicTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        assertTrue(taskManager.getEpicMap().containsValue(epic1));

        Task actualTask = taskManager.getEpicMap().get(1);
        Epic updateEpic = new Epic(
                1
                , "updateEpic"
                , "updateDesc"
        );
        assertNotEquals(updateEpic, actualTask);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateEpic)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST updateEpicTest");

        Task actualUpdateEpic = taskManager.getEpicMap().get(1);
        assertEquals(updateEpic, actualUpdateEpic);
    }

    @Test
    void getEpicTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        HashMap<Integer, Epic> actualMap = taskManager.getEpicMap();
        assertTrue(actualMap.containsValue(epic1));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/epic?id=" + epic1.getId()))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST getEpicTest");

        Task actualTask = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic1, actualTask, "Задачи различаются: GET getEpicTest");
    }

    @Test
    void getEpicsListTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        HashMap<Integer, Epic> actualMap = taskManager.getEpicMap();
        int expectedSizeMap = 2;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: getTasksListTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 GET getEpicsListTest");

        Type epicType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Task> actualEpicList = gson.fromJson(response.body(), epicType);
        assertTrue(actualEpicList.contains(epic1));
        assertTrue(actualEpicList.contains(epic2));
        assertEquals(expectedSizeMap, actualEpicList.size());
    }

    @Test
    void removeEpicTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HashMap<Integer, Epic> actualEpicMap = taskManager.getEpicMap();
        HashMap<Integer, Subtask> actualSubMap = taskManager.getSubMap();

        int expectedEpicMapSize = 1;
        int actualEpicMapSize = actualEpicMap.size();
        assertEquals(expectedEpicMapSize, actualEpicMapSize, "Отличается размер таблицы: removeEpicTest");

        int expectedSubMapSize = 2;
        int actualSubMapSize = actualSubMap.size();
        assertEquals(expectedSubMapSize, actualSubMapSize, "Отличается размер таблицы: removeEpicTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/epic?id=" + epic1.getId()))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 DELETE removeEpicTest");

        actualEpicMap = taskManager.getEpicMap();
        expectedEpicMapSize = 0;
        actualEpicMapSize = actualEpicMap.size();
        assertEquals(expectedEpicMapSize, actualEpicMapSize, "Отличается размер таблицы: removeEpicTest");

        actualSubMap = taskManager.getSubMap();
        expectedSubMapSize = 0;
        actualSubMapSize = actualSubMap.size();
        assertEquals(expectedSubMapSize, actualSubMapSize, "Отличается размер таблицы: removeEpicTest");
    }

    @Test
    void removeEpicsTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewEpic(epic2);

        HashMap<Integer, Epic> actualEpicMap = taskManager.getEpicMap();
        HashMap<Integer, Subtask> actualSubMap = taskManager.getSubMap();

        int expectedEpicMapSize = 2;
        int actualEpicMapSize = actualEpicMap.size();
        assertEquals(expectedEpicMapSize, actualEpicMapSize, "Отличается размер таблицы: removeEpicsTest");

        int expectedSubMapSize = 2;
        int actualSubMapSize = actualSubMap.size();
        assertEquals(expectedSubMapSize, actualSubMapSize, "Отличается размер таблицы: removeEpicsTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 DELETE removeEpicsTest");

        actualEpicMap = taskManager.getEpicMap();
        expectedEpicMapSize = 0;
        actualEpicMapSize = actualEpicMap.size();
        assertEquals(expectedEpicMapSize, actualEpicMapSize, "Отличается размер таблицы: removeEpicsTest");

        actualSubMap = taskManager.getSubMap();
        expectedSubMapSize = 0;
        actualSubMapSize = actualSubMap.size();
        assertEquals(expectedSubMapSize, actualSubMapSize, "Отличается размер таблицы: removeEpicsTest");
    }

    @Test
    void addNewSubTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 addNewSubtask POST");

        HashMap<Integer, Subtask> actualMap = taskManager.getSubMap();
        int expectedEpicMapSize = 1;
        assertEquals(expectedEpicMapSize, actualMap.size(), "Отличается размер таблицы addNewSub POST");

        subtask1.setId(2);
        Subtask expectedSub = subtask1;
        Subtask actualSub = taskManager.getSubMap().get(2);
        assertEquals(expectedSub, actualSub, "Эпик-Задачи различаются: addNewSub POST");

        int expectedOwnEpic = epic1.getId();
        int actualOwnEpic = subtask1.getOwnEpic();
        assertEquals(expectedOwnEpic, actualOwnEpic, "Поле ownEpic отличается от ожидаемого: addNewSub POST");

        assertTrue(epic1.getIdListSubtasks().contains(subtask1.getId()), "Subtask отсутствует в Epic");
    }

    @Test
    void updateSubTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);

        assertTrue(taskManager.getSubMap().containsValue(subtask1), "Суб-Задача не сохранилась");
        assertEquals(epic1.getStatus(), subtask1.getStatus(), "Статус у задач должен быть NEW");

        Subtask updateSubtask = new Subtask(
                2
                , TypeTask.SUBTASK
                , "updateSubTask"
                , "updateDesc"
                , StatusTask.IN_PROGRESS
                , 30
                , LocalDateTime.of(2023, 2, 1, 0, 0)
                , 1
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateSubtask)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST updateSubTest");

        assertEquals(updateSubtask.getStatus(), epic1.getStatus(), "Неверно рассчитан статус Эпик-Задачи");
        assertEquals(updateSubtask.getDuration(), epic1.getDuration(), "Неверно рассчитано время Эпик-Задачи");
        assertEquals(updateSubtask.getStartTime(), epic1.getStartTime(), "Неверно рассчитан старт Эпик-Задачи");

        Task actualUpdateSubtask = taskManager.getSubMap().get(2);
        assertEquals(updateSubtask, actualUpdateSubtask, "СУб-Задача не обновилась");
    }

    @Test
    void getSubtaskTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        HashMap<Integer, Subtask> actualMap = taskManager.getSubMap();
        assertTrue(actualMap.containsValue(subtask1));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/subtask?id=" + subtask1.getId()))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 POST getSubtaskTest");

        Subtask actualSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask1, actualSubtask, "Задачи различаются: GET getTaskTest");
    }

    @Test
    void getSubtasksListTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HashMap<Integer, Subtask> actualMap = taskManager.getSubMap();
        int expectedSizeMap = 2;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер отличается: getSubtasksListTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается: 200 GET getSubtasksListTest");

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Task> actualTasksList = gson.fromJson(response.body(), taskType);
        assertTrue(actualTasksList.contains(subtask1));
        assertTrue(actualTasksList.contains(subtask2));
        assertEquals(expectedSizeMap, actualTasksList.size());
    }

    @Test
    void removeSubTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        HashMap<Integer, Subtask> actualMap = taskManager.getSubMap();
        int expectedSizeMap = 1;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeSubTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/subtask?id=" + subtask1.getId()))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 DELETE removeSubTest");

        actualMap = taskManager.getSubMap();
        expectedSizeMap = 0;
        actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeSubTest");
    }

    @Test
    void removeSubsTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        HashMap<Integer, Subtask> actualMap = taskManager.getSubMap();
        int expectedSizeMap = 2;
        int actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeSubsTest");
        assertEquals(expectedSizeMap, epic1.getIdListSubtasks().size());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается от ожидаемого: 200 DELETE removeSubsTest");

        actualMap = taskManager.getSubMap();
        expectedSizeMap = 0;
        actualSizeMap = actualMap.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер таблицы отличается: removeTasksTest");
        assertEquals(expectedSizeMap, epic1.getIdListSubtasks().size());
    }

    @Test
    void getListAllTasksTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> actualList = taskManager.getListAllTasks();
        int expectedSizeMap = 6;
        int actualSizeMap = actualList.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер отличается: getSubtasksListTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается: 200 GET getSubtasksListTest");

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> actualTasksList = gson.fromJson(response.body(), taskType);
        assertEquals(expectedSizeMap, actualTasksList.size());
    }

    @Test
    void removeAllTasksTest() throws IOException, InterruptedException {
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> actualList = taskManager.getListAllTasks();
        int expectedSizeMap = 6;
        int actualSizeMap = actualList.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер отличается: removeAllTasksTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается: 200 GET removeAllTasksTest");

        expectedSizeMap = 0;
        actualList = taskManager.getListAllTasks();
        assertEquals(expectedSizeMap, actualList.size());

        HttpResponse<String> response2 = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        actualCode = response2.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается: 200 GET removeAllTasksTest");
    }

    @Test
    void getPriorityTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> actualList = new ArrayList<>(taskManager.getTaskMap().values());
        int expectedSizeMap = 2;
        int actualSizeMap = actualList.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер отличается: getSubtasksListTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/priority"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается: 200 GET actualList");

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> actualTasksList = gson.fromJson(response.body(), taskType);
        assertEquals(expectedSizeMap, actualTasksList.size());
        Task firstPriority = actualTasksList.get(0);
        assertEquals(task1, firstPriority);
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> actualList = new ArrayList<>(taskManager.getHistory());
        int expectedSizeMap = 0;
        int actualSizeMap = actualList.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер отличается: getHistoryTest");

        taskManager.getTask(2);
        taskManager.getTask(1);

        actualList = new ArrayList<>(taskManager.getHistory());
        expectedSizeMap = 2;
        actualSizeMap = actualList.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Ожидаемый размер отличается: getHistoryTest");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/tasks/history"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int expectedCode = 200;
        int actualCode = response.statusCode();
        assertEquals(expectedCode, actualCode, "rCode отличается: 200 GET getHistoryTest");

        Type intType = new TypeToken<List<Task>>() {}.getType();
        List<Task> actualHistory = gson.fromJson(response.body(), intType);
        assertTrue(actualHistory.contains(task1));
        assertTrue(actualHistory.contains(task2));
        int expectedSize = 2;
        assertEquals(expectedSize, actualHistory.size());
    }

    @AfterEach
    public void tearDown() throws Exception {
        kvServer.stop();
        server.stop();
    }
}
