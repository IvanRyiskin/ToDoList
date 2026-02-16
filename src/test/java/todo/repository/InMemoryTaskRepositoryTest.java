package todo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {
    Task task;
    TaskRepository<Task> repository;

    @BeforeEach
    void setUp() {
        task = new Task(200,"Beer", "Drink beer");
        repository = new InMemoryTaskRepository();
    }

    @Test
    void addTask() {
        assertTrue(repository.addTask(task));
        assertEquals(1, repository.getAllTasks().size());
    }

    @Test
    void getTaskByIdSuccess() {
        repository.addTask(task);
        Task taskfromRepo = repository.getTask(task.getID());
        assertNotNull(taskfromRepo);
    }

    @Test
    void getTaskByIdFail() {
        Task task = repository.getTask(100);
        assertNull(task);
    }

    @Test
    void getTaskByTitleSuccess() {
        repository.addTask(task);
        Task taskFromRepo = repository.getTask("Beer");
        assertEquals(task, taskFromRepo);
    }

    @Test
    void getTaskByTitleFail() {
        Task taskFromRepo = repository.getTask("Boom");
        assertNull(taskFromRepo);
    }

    @Test
    void getAllTasks() {
        assertInstanceOf(List.class, repository.getAllTasks());
    }

    @Test
    void updateTaskAllFields() {
        int taskId = task.getID();
        String updatedTitle = "Geeky";
        String updatedDescription = "Drink beer and code";
        repository.addTask(task);
        repository.updateTask(taskId, updatedTitle, updatedDescription);
        assertEquals(updatedTitle, task.getTitle());
        assertEquals(updatedDescription, task.getDescription());
    }

    @Test
    void updateTaskTitleOnly() {
        int taskId = task.getID();
        String updatedTitle = "Geeky1";
        repository.addTask(task);
        repository.updateTask(taskId, updatedTitle, null);
        assertEquals(updatedTitle, task.getTitle());
        assertEquals("Drink beer", task.getDescription());
    }

    @Test
    void updateTaskDescriptionOnly() {
        int taskId = task.getID();
        String updatedDescription = "Drink beer and code2";
        repository.addTask(task);
        repository.updateTask(taskId, null, updatedDescription);
        assertEquals("Beer", task.getTitle());
        assertEquals(updatedDescription, task.getDescription());
    }

    @Test
    void deleteTaskSuccess() {
        repository.addTask(task);
        assertNotNull(repository.getTask(task.getID()));
        repository.deleteTask(task.getID());
        assertNull(repository.getTask(task.getID()));
    }

    @Test
    void deleteTaskFail() {
        assertFalse(repository.deleteTask(100));
    }
}