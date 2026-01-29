package todo.repository;

import java.util.List;

public interface TaskRepository<Task> {
    boolean addTask(Task task);

    Task getTask(int id);

    Task getTask(String title);

    List<Task> getAllTasks();

    boolean updateTask(int id, String title, String description);

    Task deleteTask(int id);
}
