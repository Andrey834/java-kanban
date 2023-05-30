package test;

import main.managers.HistoryManager;
import main.tasks.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest <T extends HistoryManager> {
    protected T historyManager;

    @Test
    public void addTaskAndAddDuplicationTask() {
        Task task = new Task("simpleTaskH", "simpleTask");
        historyManager.add(task);

        final List<Task> actualHistoryList = historyManager.getHistory();

        assertEquals(1, actualHistoryList.size(), "Задача не добавилась");
        assertTrue(actualHistoryList.contains(task), "Задача не добавилась");

        historyManager.add(task);

        assertEquals(1, actualHistoryList.size(), "Размер истории больше при дублировании");
        assertTrue(actualHistoryList.contains(task), "Задача не заменилась при дублировании");
    }

    @Test
    public void removeTaskHistory() {
        Task task1 = new Task(1,"simple1", "simpleTask");
        Task task2 = new Task(2,"simple2", "simpleTask");
        Task task3 = new Task(3,"simple3", "simpleTask");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        final List<Task> actualHistoryList = historyManager.getHistory();

        assertEquals(3, actualHistoryList.size());

        historyManager.remove(1);
        final List<Task> actualHistoryListAfterRemoveFirstTask = historyManager.getHistory();

        assertEquals(2, actualHistoryListAfterRemoveFirstTask.size());
        assertFalse(actualHistoryListAfterRemoveFirstTask.contains(task1));

        historyManager.remove(3);
        final List<Task> actualHistoryListAfterRemoveLastTask = historyManager.getHistory();

        assertEquals(1, actualHistoryListAfterRemoveLastTask.size());
        assertFalse(actualHistoryListAfterRemoveLastTask.contains(task3));

        historyManager.remove(2);
        final List<Task> actualHistoryListAfterRemoveAllTask = historyManager.getHistory();

        assertEquals(0, actualHistoryListAfterRemoveAllTask.size());
        assertFalse(actualHistoryListAfterRemoveAllTask.contains(task2));
    }

    @Test
    public void getHistoryWhenHistoryIsEmptyTest() {
        final List<Task> actualHistoryList = historyManager.getHistory();
        assertEquals(0, actualHistoryList.size());
    }
}

