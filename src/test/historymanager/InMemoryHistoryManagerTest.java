package test.historymanager;

import main.manager.history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest extends HistoryManagerTest {

    @BeforeEach
    public void beforeEach() {
        setHistoryManager(new InMemoryHistoryManager());
    }
}