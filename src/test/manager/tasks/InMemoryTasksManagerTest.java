package test.manager.tasks;

import main.manager.Managers;
import main.manager.tasks.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTasksManagerTest extends TasksManagerTest{
    @BeforeEach
    public void beforeEach(){
        setTaskManager(new InMemoryTaskManager());
    }

}
