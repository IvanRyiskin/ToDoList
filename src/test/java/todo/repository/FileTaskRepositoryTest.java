package todo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.model.Task;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileTaskRepositoryTest {

    FileRepository fileRepository;
    TaskRepository<Task> inMemoryRepository;
    Task task;
    Path path;

    @BeforeEach
    void init() {
        inMemoryRepository = new InMemoryTaskRepositoryMap();
        task = new Task(100, "Beer", "Drink", LocalDateTime.now());
        path = Path.of("src/main/resources/tasks.txt");
    }


    @Test
    void createFile() {
        fileRepository = new FileTaskRepository(path);
        Path parent = Path.of("src/main/resources");
        Path fileName = Path.of("tasks.txt");
        assertEquals(parent, path.getParent());
        assertEquals(fileName, path.getFileName());
    }

    //    @Test
//    void getData() {
//    }
//
    @Test
    void writeData() {
        fileRepository = new FileTaskRepository(path);
        inMemoryRepository.addTask(task);
        assertTrue(fileRepository.writeData(inMemoryRepository.getTasksRepository()));
    }
}