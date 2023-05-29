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

        final List<Task> historyList = historyManager.getHistory();

        assertEquals(1, historyList.size(), "Задача не добавилась");
        assertTrue(historyList.contains(task), "Задача не добавилась");

        historyManager.add(task);

        assertEquals(1, historyList.size(), "Размер истории больше при дублировании");
        assertTrue(historyList.contains(task), "Задача не заменилась при дублировании");
    }

    @Test
    public void removeTaskHistory() {
        Task task1 = new Task(1,"simple1", "simpleTask");
        Task task2 = new Task(2,"simple2", "simpleTask");
        Task task3 = new Task(3,"simple3", "simpleTask");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        final List<Task> historyList = historyManager.getHistory();

        assertEquals(3, historyList.size());

        historyManager.remove(1);
        final List<Task> historyListAfterRemoveFirstTask = historyManager.getHistory();

        assertEquals(2, historyListAfterRemoveFirstTask.size());
        assertFalse(historyListAfterRemoveFirstTask.contains(task1));

        historyManager.remove(3);
        final List<Task> historyListAfterRemoveLastTask = historyManager.getHistory();

        assertEquals(1, historyListAfterRemoveLastTask.size());
        assertFalse(historyListAfterRemoveLastTask.contains(task3));

        historyManager.remove(2);
        final List<Task> historyListAfterRemoveAllTask = historyManager.getHistory();

        assertEquals(0, historyListAfterRemoveAllTask.size());
        assertFalse(historyListAfterRemoveAllTask.contains(task2));
    }

    @Test
    public void getHistoryWhenHistoryIsEmptyTest() {
        final List<Task> historyList = historyManager.getHistory();
        assertEquals(0, historyList.size());
    }
}

