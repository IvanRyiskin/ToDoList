package todo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.model.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryMapTest {
    Task task;
    TaskRepository<Task> repository;

    @BeforeEach
    void setUp() {
        task = new Task(200, "Beer", "Drink beer", LocalDateTime.now());
        repository = new InMemoryTaskRepositoryMap();
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
        String updatedTitle = "Geeky";
        String updatedDescription = "Drink beer and code";
        repository.addTask(task);
        Task updatedTask = new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE());
        repository.updateTask(task, updatedTask, updatedTitle, updatedDescription, LocalDateTime.now());
        updatedTask = repository.getTask(task.getID());
        assertEquals(updatedTitle, updatedTask.getTitle());
        assertEquals(updatedDescription, updatedTask.getDescription());
    }

    @Test
    void updateTaskTitleOnly() {
        String updatedTitle = "Geeky1";
        repository.addTask(task);
        Task updatedTask = new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE());
        repository.updateTask(task, updatedTask, updatedTitle, null, LocalDateTime.now());
        updatedTask = repository.getTask(task.getID());
        assertEquals(updatedTitle, updatedTask.getTitle());
        assertEquals("Drink beer", updatedTask.getDescription());
    }

    @Test
    void updateTaskDescriptionOnly() {
        String updatedDescription = "Drink beer and code2";
        repository.addTask(task);
        Task updatedTask = new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE());
        repository.updateTask(task, updatedTask, null, updatedDescription, LocalDateTime.now());
        updatedTask = repository.getTask(task.getID());
        assertEquals("Beer", updatedTask.getTitle());
        assertEquals(updatedDescription, updatedTask.getDescription());
    }

    @Test
    void deleteTaskSuccess() {
        repository.addTask(task);
        assertNotNull(repository.getTask(task.getID()));
        repository.deleteTask(task);
        assertNull(repository.getTask(task.getID()));
    }

    @Test
    void deleteTaskFail() {
        task = new Task(20, "Beer", "Drink beer", LocalDateTime.now());
        assertFalse(repository.deleteTask(task));
    }
}