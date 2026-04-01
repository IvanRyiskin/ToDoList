package todo.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

class FileTaskRepositoryTest {

    FileTaskRepository fileRepository;
    TaskRepository<Task> inMemoryRepository;
    Task task;
    Path path;
    static List<Path> tempFiles = new ArrayList<>();

    @BeforeEach
    void init() {
        inMemoryRepository = new InMemoryTaskRepositoryMap();
        task = new Task(100, "Beer", "Drink", LocalDateTime.now());
        path = Path.of("src/main/test_resources/tasks.txt");
        fileRepository = new FileTaskRepository(path);
    }

    @AfterAll
    static void delete() {
        tempFiles.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void createFile() {
        Path parent = Path.of("src/main/test_resources");
        Path fileName = Path.of("tasks.txt");
        assertEquals(parent, path.getParent());
        assertEquals(fileName, path.getFileName());
        tempFiles.add(path);
    }

    @Test
    void getDataFromEmptyFile() {
        Path emtyPath = Path.of("src/main/test_resources/emptyTasks.txt");
        FileTaskRepository emptyFileRepository = new FileTaskRepository(emtyPath);
        ConcurrentMap<Integer, Task> getFile = emptyFileRepository.getData();
        assertTrue(getFile.isEmpty());
        tempFiles.add(emtyPath);
    }

    @Test
    void getDataFromNotEmtyFile() {
        ConcurrentMap<Integer, Task> getFile = fileRepository.getData();
        assertFalse(getFile.isEmpty());
    }

    @Test
    void writeData() {
        inMemoryRepository.addTask(task);
        assertTrue(fileRepository.writeData(inMemoryRepository.getTasksRepository()));
        tempFiles.add(path);
    }

    // тест для проверки десериализации
    @Test
    void getDataFro1mFile() {
        assertFalse(fileRepository.getData().isEmpty());
        System.out.println(fileRepository.getData());
    }
}