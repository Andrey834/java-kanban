package test;

import main.managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagersTest extends HistoryManagersTest<InMemoryHistoryManager> {

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }
}

