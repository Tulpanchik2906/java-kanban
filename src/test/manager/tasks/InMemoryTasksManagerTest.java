package test.manager.tasks;

import main.manager.Managers;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTasksManagerTest extends TasksManagerTest{
    @BeforeEach
    public void beforeEach(){
        setTaskManager(Managers.getDefault());
    }

}
