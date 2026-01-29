package todo.service;

import todo.model.Task;
import todo.repository.TaskRepository;

import java.util.List;

public class TaskService {
    private final TaskRepository<Task> repository;

    public TaskService(TaskRepository<Task> repository) {
        this.repository = repository;
    }

    public Task createTask(String title, String description) {
        return new Task(title, description);
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

    public boolean updateTask(int id, String updatedTitle, String updatedDescription) {
        return repository.updateTask(id, updatedTitle, updatedDescription);
    }

    public boolean deleteTask(int id) {
        return repository.deleteTask(id) != null;
    }
}
