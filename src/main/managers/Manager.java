package main.managers;

public class Manager {
    public static TaskManager getDefault() {
        return new main.managers.InMemoryTaskManager();
    }

    public static main.managers.HistoryManager getDefaultHistory() {
        return new main.managers.InMemoryHistoryManager();
    }
}
