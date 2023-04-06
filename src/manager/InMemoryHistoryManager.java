package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final Integer MAX_SIZE_HISTORY = 10;
    private final List<Task> historyTasks = new ArrayList<>(MAX_SIZE_HISTORY);

    @Override
    public void add(Task task) {
        if (historyTasks.size() == MAX_SIZE_HISTORY) {
            historyTasks.remove(0);
            historyTasks.add(task);
        } else {
            historyTasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }
}
