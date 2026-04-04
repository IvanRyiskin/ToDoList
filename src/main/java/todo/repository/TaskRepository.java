package todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface TaskRepository<Task> {

    ConcurrentMap<Integer, Task> getTasksRepository();

    boolean addTask(Task task);

    Task getTask(int id);

    Task getTask(String title);

    List<Task> getAllTasks();

    boolean updateTask(Task originTask, Task updatedTask, String title, String description, LocalDateTime localDateTime);

    boolean deleteTask(Task task);
}
