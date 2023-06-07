package test;

import main.managers.TaskManager;
import main.tasks.StatusTask;
import main.tasks.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagersTest<T extends TaskManager> {
    protected T manager;

    @Test
    void addNewTask() {
        Task task = new Task("test addNewTask", "test addNewTask description");
        manager.addNewTask(task);

        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = manager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("test addNewEpic", "test addNewEpic description");
        manager.addNewEpic(epic);

        final Task savedEpic = manager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic, savedEpic, "Задачи не совпадают");

        final List<Task> tasks = manager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSub() {
        Epic epicTask = new Epic("test addNewEpic", "test addNewEpic description");
        Subtask subTask = new Subtask(
                "test addNewSub",
                "test addNewSub desc",
                15,
                "2023-05-26 21:00",
                1
        );
        manager.addNewEpic(epicTask);
        manager.addNewSubtask(subTask);
        final Epic savedEpic = manager.getEpic(epicTask.getId());
        final Subtask savedSubTask = manager.getSubtask(subTask.getId());
        final int expectedOwnSubFromEpic = savedEpic.getId();
        final int actualOwnEpicFromSubtask = savedSubTask.getOwnEpic();
        final int expectedIdInIdSubListFromEpic = savedSubTask.getId();


        assertEquals(epicTask, savedEpic, "Эпик-Задачи не совпадают");
        assertEquals(subTask, savedSubTask, "Суб-Задачи не совпадают");
        assertEquals(expectedOwnSubFromEpic, actualOwnEpicFromSubtask, "ID Эпика отличается от ожидаемого");
        assertTrue(savedEpic.getIdListSubtasks().contains(expectedIdInIdSubListFromEpic), "Отсутствует ID");
    }

    @Test
    void updateTaskTest() {
        Task expectedTask = new Task(1,
                TypeTask.TASK,
                "UpdTitle",
                "UpdDesc",
                StatusTask.IN_PROGRESS,
                15,
                LocalDateTime.parse("2023-07-25T12:45"));

        manager.addNewTask(new Task("simpleTask", "descTask", 30, "2023-05-25 12:30"));
        manager.updateTask(expectedTask);
        Task savedTask = manager.getTask(1);
        assertEquals(expectedTask, savedTask, "Задачи различаются");
    }

    @Test
    void updateEpicTask() {
        Epic expectedEpic = new Epic(1, "UpdateTitle", "UpdateDesc");
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.updateEpic(expectedEpic);
        Epic savedEpic = manager.getEpic(1);
        assertEquals(expectedEpic, savedEpic, "Задачи различаются");
    }

    @Test
    void updateSubTask() {
        Subtask expectedTaskProgress = new Subtask(
                2,
                TypeTask.SUBTASK,
                "Sub",
                "SubD",
                StatusTask.IN_PROGRESS,
                30,
                LocalDateTime.parse("2023-05-01T00:00"),
                1
        );

        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask(
                "createSub",
                "descSubTask",
                15,
                "2023-01-01 15:00",
                1)
        );
        manager.updateSubtask(expectedTaskProgress);

        Epic epic = manager.getEpic(1);
        Subtask savedSubtask = manager.getSubtask(2);
        ;

        assertEquals(epic.getId(), savedSubtask.getOwnEpic(), "Отличается эпик в Суб-Задаче");
        assertEquals(expectedTaskProgress, savedSubtask, "Задачи отличаются");
    }

    @Test
    void removeTask() {
        manager.addNewTask(new Task("simpleTask", "descTask", 15, "2023-05-25 12:30"));

        assertEquals(1, manager.getListAllTasks().size());
        assertEquals(1, manager.getTaskMap().size());

        manager.removeTask(1);

        assertEquals(0, manager.getSubMap().size());
    }

    @Test
    void removeSub() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub", "SubD", 15, "2023-05-29 12:15", 1));
        assertEquals(2, manager.getListAllTasks().size());
        manager.removeSubtask(2);
        assertEquals(0, manager.getSubMap().size());
    }

    @Test
    void removeEpic() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub", "SubD", 15, "2023-05-29 12:15", 1));
        assertEquals(2, manager.getListAllTasks().size());

        assertEquals(1, manager.getEpicMap().size());
        assertEquals(1, manager.getSubMap().size());

        manager.removeEpic(1);

        assertEquals(0, manager.getEpicMap().size(), "Эпик-Задача не удалена");
        assertEquals(0, manager.getSubMap().size(), "Осталась Суб-Задача Эпика");
    }

    @Test
    void removeAllTasks() {
        manager.addNewTask(new Task("simpleTask", "descTask", 15, "2023-05-25 12:30"));
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub", "SubD", 15, "2023-05-29 12:15", 2));

        assertEquals(3, manager.getListAllTasks().size());

        manager.removeAllTasks();

        assertTrue(manager.getTaskMap().isEmpty(), "Остались задачи");
        assertTrue(manager.getEpicMap().isEmpty(), "Остались Эпик-Задачи");
        assertTrue(manager.getSubMap().isEmpty(), "Остались Суб-Задачи");
        assertTrue(manager.getHistory().isEmpty(), "В истории остались задачи");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Остались задачи в списке приоритета");
    }

    @Test
    void getTaskTest() {
        manager.addNewTask(new Task("simpleTask", "descTask", 15, "2023-05-25 12:30"));

        Task task = manager.getTask(1);
        assertNotNull(manager.getTaskMap().get(task.getId()), "Отсутствует задача");
        assertNotNull(task, "Отсутствует задача");

        final List<Task> historyList = manager.getHistory();

        assertEquals(1, historyList.size());
        assertTrue(historyList.contains(task));
    }

    @Test
    void getEpicTest() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));

        Epic epic = manager.getEpic(1);
        assertNotNull(manager.getEpicMap().get(epic.getId()), "Отсутствует Эпик-Задача");
        assertNotNull(epic, "Отсутствует Эпик-Задача");

        final List<Task> historyList = manager.getHistory();

        assertEquals(1, historyList.size());
        assertTrue(historyList.contains(epic));
    }

    @Test
    void getSubtaskTest() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub", "SubD", 15, "2023-05-29 12:15", 1));

        Subtask sub = manager.getSubtask(2);
        assertNotNull(manager.getSubMap().get(sub.getId()), "Отсутствует Суб-Задача");
        assertNotNull(sub, "Отсутствует Суб-Задача");

        final List<Task> historyList = manager.getHistory();

        assertEquals(1, historyList.size());
        assertTrue(historyList.contains(sub));
    }

    @Test
    void getSubListFromEpicTest() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub1", "SubD", 15, "2023-05-28 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub2", "SubD", 15, "2023-05-29 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub3", "SubD", 15, "2023-05-30 12:15", 1));

        Epic epic = manager.getEpic(1);
        Subtask sub1 = manager.getSubtask(2);
        Subtask sub2 = manager.getSubtask(3);
        Subtask sub3 = manager.getSubtask(4);

        assertNotNull(epic, "Эпик-Задача не найдена");
        assertNotNull(sub1, "Суб-Задача sub1 не найдена");
        assertNotNull(sub2, "Суб-Задача sub2 не найдена");
        assertNotNull(sub3, "Суб-Задача sub3 не найдена");

        final List<Integer> expectedListSubtask = List.of(sub1.getId(), sub2.getId(), sub3.getId());
        final List<Integer> listSubtask = manager.getSubListFromEpic(epic.getId());

        assertArrayEquals(expectedListSubtask.toArray(), listSubtask.toArray(), "Список Суб-Задач отличается");
        assertEquals(3, listSubtask.size());
    }

    @Test
    void getHistoryTest() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub1", "SubD", 15, "2023-05-28 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub2", "SubD", 15, "2023-05-29 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub3", "SubD", 15, "2023-05-30 12:15", 1));

        Subtask sub2 = manager.getSubtask(3);
        Subtask sub3 = manager.getSubtask(4);
        Subtask sub1 = manager.getSubtask(2);
        Epic epic = manager.getEpic(1);

        assertNotNull(epic, "Эпик-Задача не найдена");
        assertNotNull(sub1, "Суб-Задача sub1 не найдена");
        assertNotNull(sub2, "Суб-Задача sub2 не найдена");
        assertNotNull(sub3, "Суб-Задача sub3 не найдена");

        final List<Task> expectedListHistory = List.of(sub2, sub3, sub1, epic);
        final List<Task> listHistory = manager.getHistory();

        assertArrayEquals(expectedListHistory.toArray(), listHistory.toArray(), "Неверный список истории");
        assertEquals(4, listHistory.size(), "Отличается размер списка истории");
    }

    @Test
    void getListAllTasksTest() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub1", "SubD", 15, "2023-05-28 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub2", "SubD", 15, "2023-05-29 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub3", "SubD", 15, "2023-05-30 12:15", 1));

        Subtask sub2 = manager.getSubtask(3);
        Subtask sub3 = manager.getSubtask(4);
        Subtask sub1 = manager.getSubtask(2);
        Epic epic = manager.getEpic(1);

        assertNotNull(epic, "Эпик-Задача не найдена");
        assertNotNull(sub1, "Суб-Задача sub1 не найдена");
        assertNotNull(sub2, "Суб-Задача sub2 не найдена");
        assertNotNull(sub3, "Суб-Задача sub3 не найдена");

        final List<Task> expectedListTasks = List.of(epic, sub1, sub2, sub3);
        final List<Task> listTasks = manager.getListAllTasks();

        assertArrayEquals(expectedListTasks.toArray(), listTasks.toArray(), "Неверный список задач");
        assertEquals(4, listTasks.size(), "Отличается размер списка задач");
    }

    @Test
    void getPrioritizedTasksTest() {
        manager.addNewEpic(new Epic("simpleTask", "descTask"));
        manager.addNewSubtask(new Subtask("Sub1", "SubD", 15, "2023-05-28 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub2", "SubD", 15, "2023-05-29 12:15", 1));
        manager.addNewSubtask(new Subtask("Sub3", "SubD", 15, "2023-05-30 12:15", 1));

        Epic epic = manager.getEpic(1);
        Subtask expectedFirstTask = manager.getSubtask(2);
        Subtask expectedLastTask = manager.getSubtask(4);

        assertNotNull(epic, "Эпик-Задача не найдена");
        assertNotNull(expectedFirstTask, "Суб-Задача sub1 не найдена");
        assertNotNull(expectedLastTask, "Суб-Задача sub2 не найдена");

        final List<Task> listTasks = manager.getPrioritizedTasks();

        assertEquals(3, listTasks.size());

        Subtask actualFirstSubtask = (Subtask) listTasks.get(0);
        Subtask actualLastSubtask = (Subtask) listTasks.get(listTasks.size() - 1);

        assertEquals(expectedFirstTask, actualFirstSubtask, "Неверный приоритета первой задачи");
        assertEquals(expectedLastTask, actualLastSubtask, "Неверный приоритета второй задачи");
    }

    @Test
    void whenIdSubListFromEpicIsEmptyThenStatusNew() {
        final int sizeEmptyIdListSubFromEpic = 0;
        Epic epicTask = new Epic("test addNewEpic", "test addNewEpic description");
        manager.addNewEpic(epicTask);

        final Epic savedEpicTask = manager.getEpic(epicTask.getId());

        assertNotNull(savedEpicTask, "Эпик-Задача не найдена");
        assertEquals(sizeEmptyIdListSubFromEpic, savedEpicTask.getIdListSubtasks().size(), "Присутствуют подзадачи");
        assertEquals(StatusTask.NEW, savedEpicTask.getStatus(), "Статус изменен");
    }

    @Test
    void whenEpicHasAllSubtasksStatusNewThenEpicStatusNew() {
        final int idListSubtaskFromEpicSize2 = 2;
        Epic epicTask = new Epic("test addNewEpic", "test addNewEpic description");
        Subtask subTask1 = new Subtask("test addNewSub1", "test addNewSub1 description1"
                , 30, "2023-05-21 15:00", 1);
        Subtask subTask2 = new Subtask("test addNewSub2", "test addNewSub2 description2"
                , 30, "2023-05-21 16:00", 1);
        manager.addNewEpic(epicTask);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);

        final Epic savedEpic = manager.getEpic(epicTask.getId());
        final Subtask savedSub1 = manager.getSubtask(subTask1.getId());
        final Subtask savedSub2 = manager.getSubtask(subTask2.getId());

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(idListSubtaskFromEpicSize2, savedEpic.getIdListSubtasks().size(), "Нет подзадач");
        assertEquals(StatusTask.NEW, savedSub1.getStatus(), "У первой подзадачи отличается статус");
        assertEquals(StatusTask.NEW, savedSub2.getStatus(), "У второй подзадачи отличается статус");
        assertEquals(StatusTask.NEW, savedEpic.getStatus(), "Отличается статус подзадачи у Эпика");
    }

    @Test
    void whenEpicHasAllSubtasksStatusDoneThenEpicStatusDone() {
        final int idListSubtaskFromEpicSize2 = 2;
        Epic epicTask = new Epic("test addNewEpic", "test addNewEpic description");
        Subtask subTask1 = new Subtask("test addNewSub1", "test addNewSub1 description1"
                , 30, "2023-05-21 15:00", 1);
        Subtask subTask2 = new Subtask("test addNewSub2", "test addNewSub2 description2"
                , 30, "2023-05-21 16:00", 1);

        manager.addNewEpic(epicTask);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);

        manager.updateSubtask(new Subtask(2, StatusTask.DONE));
        manager.updateSubtask(new Subtask(3, StatusTask.DONE));

        final Epic savedEpicTask = manager.getEpic(epicTask.getId());
        final Subtask savedSubTask1 = manager.getSubtask(subTask1.getId());
        final Subtask savedSubTask2 = manager.getSubtask(subTask2.getId());

        assertNotNull(savedEpicTask, "Задача не найдена");
        assertNotNull(savedSubTask1, "Подзадача 1 не найдена");
        assertNotNull(savedSubTask2, "Подзадача 2 не найдена");

        assertEquals(idListSubtaskFromEpicSize2, savedEpicTask.getIdListSubtasks().size(), "Присутствуют подзадачи");
        assertEquals(StatusTask.DONE, savedSubTask1.getStatus(), "У первой подзадачи отличается статус");
        assertEquals(StatusTask.DONE, savedSubTask2.getStatus(), "У второй подзадачи отличается статус");
        assertEquals(StatusTask.DONE, savedEpicTask.getStatus(), "Отличается статус подзадачи у Эпика");
    }

    @Test
    void whenEpicHasAllSubtasksDifferentStatusThenEpicStatusInProgress() {
        manager.removeAllTasks();
        final int idListSubtaskFromEpicSize2 = 2;
        Epic epicTask = new Epic("test addNewEpic", "test addNewEpic description");
        Subtask subTask1 = new Subtask("test addNewSub1", "test addNewSub1 description1"
                , 30, "2023-05-21 15:00", 1);
        Subtask subTask2 = new Subtask("test addNewSub2", "test addNewSub2 description2"
                , 30, "2023-05-21 16:00", 1);

        manager.addNewEpic(epicTask);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);

        manager.updateSubtask(new Subtask(2, StatusTask.DONE));
        manager.updateSubtask(new Subtask(3, StatusTask.IN_PROGRESS));

        final Epic savedEpicTask = manager.getEpic(epicTask.getId());
        final Subtask savedSubTask1 = manager.getSubtask(subTask1.getId());
        final Subtask savedSubTask2 = manager.getSubtask(subTask2.getId());

        assertNotNull(savedEpicTask, "Задача не найдена");
        assertNotNull(savedSubTask1, "Подзадача 1 не найдена");
        assertNotNull(savedSubTask2, "Подзадача 2 не найдена");

        assertEquals(idListSubtaskFromEpicSize2, savedEpicTask.getIdListSubtasks().size(), "Присутствуют подзадачи");
        assertEquals(StatusTask.DONE, savedSubTask1.getStatus(), "У первой подзадачи отличается статус");
        assertEquals(StatusTask.IN_PROGRESS, savedSubTask2.getStatus(), "У второй подзадачи отличается статус");
        assertEquals(StatusTask.IN_PROGRESS, savedEpicTask.getStatus(), "Отличается статус подзадачи у Эпика");
    }

    @AfterEach
    public void AfterEach() {
        manager.removeAllTasks();
    }
}
