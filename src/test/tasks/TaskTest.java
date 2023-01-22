package test.tasks;

import main.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.TestDataUtil;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testGetEndTimeWhenNotNullStartTime() {
        Task task = TestDataUtil.createDefaultTask();
        task.setDuration(60);
        task.setStartTime(LocalDateTime.parse("2023-01-20T12:01"));
        Assertions.assertEquals(LocalDateTime.parse("2023-01-20T13:01"), task.getEndTime());
    }

    @Test
    void testGetEndTimeWhenNullStartTime() {
        Task task = TestDataUtil.createDefaultTask();
        Assertions.assertNull( task.getEndTime());
    }
}