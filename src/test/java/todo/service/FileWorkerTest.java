package todo.service;

import org.junit.jupiter.api.*;
import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileTaskRepository;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;
import todo.view.ConsoleView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileWorkerTest {

    static BlockingQueue<FileTask> blockingQueue;
    static FileTaskRepository fileRepository;
    static ConcurrentMap<Integer, Task> taskFileRepository;
    static TaskRepository<Task> inMemoryRepository;
    static TaskService service;
    static ApplicationCoordinator coordinator;
    static ConsoleView view;
    static FileWorker fileWorker;
    static Path path = Path.of("src/main/test_resources/tasks.txt");
    static Task taskToAddAndUpdate;
    static Task taskToDelete;

    @BeforeAll
    static void setUp() {
        taskToAddAndUpdate = new Task(100, "Beer", "Drink", LocalDateTime.now());
        taskToDelete = new Task(300, "Leer", "Lrink", LocalDateTime.now());
        blockingQueue = new LinkedBlockingQueue<>();
        fileRepository = new FileTaskRepository(path);
        taskFileRepository = fileRepository.getData();
        inMemoryRepository = new InMemoryTaskRepositoryMap(taskFileRepository);
        service = new TaskService(inMemoryRepository, blockingQueue);
        coordinator = new ApplicationCoordinator(service, view);
        view = new ConsoleView(service, blockingQueue);
        fileWorker = new FileWorker(fileRepository, blockingQueue, inMemoryRepository, coordinator);
        fileWorker.start();
    }

    @AfterAll
    static void delete() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void TestAddTask() {
        service.addTask(taskToAddAndUpdate);
        service.addTask(taskToDelete);
        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> fileRepository.getData().values().stream()
                        .anyMatch(t -> "Beer".equals(t.getTitle()) && taskToAddAndUpdate.equals(t)));
        ConcurrentMap<Integer, Task> newTaskFileRepository = fileRepository.getData();
        TaskRepository<Task> inMemoryRepository = new InMemoryTaskRepositoryMap(newTaskFileRepository);
        BlockingQueue<FileTask> newBlock = new LinkedBlockingQueue<>();
        TaskService newService = new TaskService(inMemoryRepository, newBlock);

        assertEquals(taskToAddAndUpdate, newService.getTask("Beer"));
    }

    @Test
    @Order(2)
    void TestUpdateTask() {
        service.updateTask(taskToAddAndUpdate, "TTT", "PPP");
        taskToAddAndUpdate = service.getTask("TTT");
        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> fileRepository.getData().values().stream()
                        .anyMatch(t -> "TTT".equals(t.getTitle()) && taskToAddAndUpdate.equals(t)));
        ConcurrentMap<Integer, Task> newTaskFileRepository = fileRepository.getData();
        TaskRepository<Task> inMemoryRepository = new InMemoryTaskRepositoryMap(newTaskFileRepository);
        BlockingQueue<FileTask> newBlock = new LinkedBlockingQueue<>();
        TaskService newService = new TaskService(inMemoryRepository, newBlock);

        assertEquals(taskToAddAndUpdate, newService.getTask("TTT"));
    }

    @Test
    @Order(3)
    void TestDeleteTask() {
        service.deleteTask(taskToDelete);
        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> fileRepository.getData().values().stream()
                        .noneMatch(t -> "Leer".equals(t.getTitle()) && taskToAddAndUpdate.equals(t)));
        ConcurrentMap<Integer, Task> newTaskFileRepository = fileRepository.getData();
        TaskRepository<Task> inMemoryRepository = new InMemoryTaskRepositoryMap(newTaskFileRepository);
        BlockingQueue<FileTask> newBlock = new LinkedBlockingQueue<>();
        TaskService newService = new TaskService(inMemoryRepository, newBlock);

        assertNull(newService.getTask("Leer"));
    }

    @Test
    @Order(4)
    void TestCheckFile() {
        ConcurrentMap<Integer, Task> newFile = fileRepository.getData();
        assertFalse(newFile.isEmpty());
        assertTrue((fileRepository.getData().size() == 1));
        System.out.println(newFile.size());
        System.out.println(newFile.values());
    }
}