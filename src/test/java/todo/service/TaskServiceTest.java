package todo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.model.Task;
import todo.repository.InMemoryTaskRepository;
import todo.repository.TaskRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    static TaskService service;
    static TaskRepository<Task> repository;
    Task task;

    @BeforeAll
    static void init() {
        repository = new InMemoryTaskRepository();
        service = new TaskService(repository);
    }

    @BeforeEach
    void setUp() {
        task = new Task((int) (Math.random() * 1000),"Task", "Description");
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
        Task result  = service.getTask(task.getID());
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
        int taskId = task.getID();
        String updatedTitle = "Geeky";
        String updatedDescription = "Drink beer and code";
        service.addTask(task);
        service.updateTask(taskId, updatedTitle, updatedDescription);
        assertEquals(updatedTitle, task.getTitle());
        assertEquals(updatedDescription, task.getDescription());
    }

    @Test
    void updateTaskTitleOnly() {
        int taskId = task.getID();
        String updatedTitle = "Task1";
        service.addTask(task);
        service.updateTask(taskId, updatedTitle, null);
        assertEquals(updatedTitle, task.getTitle());
        assertEquals("Description", task.getDescription());
    }

    @Test
    void updateTaskDescriptionOnly() {
        int taskId = task.getID();
        String updatedDescription = "Drink beer and code";
        service.addTask(task);
        service.updateTask(taskId, null, updatedDescription);
        assertEquals("Task", task.getTitle());
        assertEquals(updatedDescription, task.getDescription());
    }

    @Test
    void deleteTaskSuccess() {
        service.addTask(task);
        assertNotNull(service.getTask(task.getID()));
        service.deleteTask(task.getID());
        assertNull(service.getTask(task.getID()));
    }

    @Test
    void deleteTaskFail() {
        assertFalse(service.deleteTask(100));
    }
}