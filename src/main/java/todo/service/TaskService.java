package todo.service;

import todo.model.FileTask;
import todo.model.Task;
import todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static todo.model.FileAction.*;

public class TaskService {
    private final BlockingQueue<FileTask> blockingQueue;
    private TaskRepository<Task> repository;
    private int idCounter = 1;

    public TaskService(TaskRepository<Task> repository, BlockingQueue<FileTask> blockingQueue) {
        this.repository = repository;
        this.blockingQueue = blockingQueue;
    }

    public void changeRepository(TaskRepository<Task> newRepository) {
        repository = newRepository;
    }

    private static Task copyTask(Task task) {
        return new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE(), task.getUpdateDate());
    }

    public Task createTask(String title, String description) {
        return new Task(idCounter++, title, description, LocalDateTime.now());
    }

    public boolean addTask(Task task) {
        boolean result = repository.addTask(task);
        try {
            blockingQueue.put(new FileTask(ADD, task));
        } catch (InterruptedException e) {
            try {
                blockingQueue.put(new FileTask(EXIT));
            } catch (InterruptedException ex) {
                // Подумать над экстренным сохранением в файл всех данных
                System.err.println("Произошло непредвиденное завершение работы программы. Завершаем все процессы...");
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    public Task getTask(int id) {
        return repository.getTask(id);
    }

    public Task getTask(String title) {
        return repository.getTask(title);
    }

    public List<Task> getAllTasks() {
        return repository.getAllTasks();
    }

    public boolean updateTask(Task task, String updatedTitle, String updatedDescription) {
        Task updatedTask = copyTask(task);
        boolean result = repository.updateTask(task, updatedTask, updatedTitle, updatedDescription, LocalDateTime.now());
        try {
            blockingQueue.put(new FileTask(UPDATE, task));
        } catch (InterruptedException e) {
            try {
                blockingQueue.put(new FileTask(EXIT));
            } catch (InterruptedException ex) {
                // Подумать над экстренным сохранением в файл всех данных
                System.err.println("Произошло непредвиденное завершение работы программы. Завершаем все процессы...");
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    public boolean deleteTask(Task task) {
        boolean result = repository.deleteTask(task);
        try {
            blockingQueue.put(new FileTask(DELETE, task));
        } catch (InterruptedException e) {
            try {
                blockingQueue.put(new FileTask(EXIT));
            } catch (InterruptedException ex) {
                // Подумать над экстренным сохранением в файл всех данных
                System.err.println("Произошло непредвиденное завершение работы программы. Завершаем все процессы...");
                throw new RuntimeException(ex);
            }
        }
        return result;
    }
}
