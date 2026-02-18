package todo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task;

    @BeforeEach
    void init() {
        task  = new Task(100,"Beer", "Drink", LocalDateTime.now());
    }

    @Test
    void setAndGetTitle() {
        task.setTitle("Grog");
        assertEquals("Grog", task.getTitle());
    }

    @Test
    void getID() {
        assertNotEquals(0, task.getID());
    }

    @Test
    void getCREATEDATE() {
        assertNotNull(task.getCREATEDATE());
        System.out.println("Дата создания: " + task.getCREATEDATE());
    }

    @Test
    void setAndGetDescription() {
        task.setDescription("Eat");
        assertEquals("Eat", task.getDescription());
    }

    @Test
    void setAndGetUpdateDate() {
        task.setUpdateDate(LocalDateTime.now());
        assertNotNull(task.getUpdateDate());
        System.out.println("Дата обновления: " + task.getUpdateDate() + "\n");
    }
}