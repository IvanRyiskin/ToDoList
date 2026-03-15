package todo.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository<Task> {
    boolean addTask(Task task);

    Task getTask(int id);

    Task getTask(String title);

    List<Task> getAllTasks();

    boolean updateTask(Task originTask, Task updatedTask, String title, String description, LocalDateTime localDateTime);

    boolean deleteTask(Task task);
}
