package todo.service;

import todo.model.Task;
import todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private final TaskRepository<Task> repository;
    private int idCounter = 1;

    private static Task copyTask(Task task){
        return new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE());
    }

    public TaskService(TaskRepository<Task> repository) {
        this.repository = repository;
    }

    public Task createTask(String title, String description) {
        return new Task(idCounter++, title, description, LocalDateTime.now());
    }

    public boolean addTask(Task task) {
        return repository.addTask(task);
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
        return repository.updateTask(task, updatedTask, updatedTitle, updatedDescription, LocalDateTime.now());
    }

    public boolean deleteTask(Task task) {
        return repository.deleteTask(task);
    }
}
