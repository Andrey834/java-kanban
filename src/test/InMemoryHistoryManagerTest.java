package test;

import main.managers.InMemoryHistoryManager;
import main.managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }
}

