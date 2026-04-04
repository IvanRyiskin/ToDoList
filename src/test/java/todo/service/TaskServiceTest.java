package todo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.model.FileTask;
import todo.model.Task;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    static TaskService service;
    static TaskRepository<Task> repository;
    static BlockingQueue<FileTask> blockingQueue;
    Task task;

    @BeforeAll
    static void init() {
        BlockingQueue<FileTask> blockingQueue = new LinkedBlockingQueue<>();
        repository = new InMemoryTaskRepositoryMap();
        service = new TaskService(repository, blockingQueue);
    }

    @BeforeEach
    void setUp() {
        task = new Task((int) (Math.random() * 1000), "Task", "Description", LocalDateTime.now());
    }

    @Test
    void createTask() {
        Task task = service.createTask("Task 1", "Description 1");
        assertNotNull(task);
        assertEquals("Task 1", task.getTitle());
        assertEquals("Description 1", task.getDescription());
    }

    @Test
    void addTask() {
        boolean result = service.addTask(task);
        assertTrue(result);
    }

    @Test
    void getTaskById() {
        service.addTask(task);
        Task result = service.getTask(task.getID());
        assertEquals(task, result);
    }

    @Test
    void testGetTaskByTitle() {
        Task task = service.createTask("Task2", "Description2");
        service.addTask(task);
        Task result = service.getTask(task.getTitle());
        assertEquals(task, result);
    }

    @Test
    void getAllTasks() {
        assertInstanceOf(List.class, service.getAllTasks());
    }

    @Test
    void updateTaskAllFields() {
        String updatedTitle = "Geeky";
        String updatedDescription = "Drink beer and code";
        service.addTask(task);
        service.updateTask(task, updatedTitle, updatedDescription);
        Task updatedTask = repository.getTask(task.getID());
        assertEquals(updatedTitle, updatedTask.getTitle());
        assertEquals(updatedDescription, updatedTask.getDescription());
    }

    @Test
    void updateTaskTitleOnly() {
        String updatedTitle = "Task1";
        service.addTask(task);
        service.updateTask(task, updatedTitle, null);
        Task updatedTask = repository.getTask(task.getID());
        assertEquals(updatedTitle, updatedTask.getTitle());
        assertEquals("Description", updatedTask.getDescription());
    }

    @Test
    void updateTaskDescriptionOnly() {
        String updatedDescription = "Drink beer and code";
        service.addTask(task);
        service.updateTask(task, null, updatedDescription);
        Task updatedTask = repository.getTask(task.getID());
        assertEquals("Task", updatedTask.getTitle());
        assertEquals(updatedDescription, updatedTask.getDescription());
    }

    @Test
    void deleteTaskSuccess() {
        service.addTask(task);
        assertNotNull(service.getTask(task.getID()));
        service.deleteTask(task);
        assertNull(service.getTask(task.getID()));
    }

    @Test
    void deleteTaskFail() {
        task = new Task(30, "Beer", "Drink beer", LocalDateTime.now());
        assertFalse(service.deleteTask(task));
    }
}