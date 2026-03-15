package todo.repository;

import todo.model.Task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTaskRepositoryMap implements TaskRepository<Task>, Serializable {
    ConcurrentMap<Integer, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public boolean addTask(Task task) {
        return tasks.putIfAbsent(task.getID(), task) == null;
    }

    @Override
    public Task getTask(int id) {
        return tasks.getOrDefault(id, null);
    }

    @Override
    public Task getTask(String title) {
        for (Task task : tasks.values()) {
            if (task.getTitle().equals(title)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public boolean updateTask(Task task, Task updatedTask, String updatedTitle, String updatedDescription, LocalDateTime localDateTime) {
        if (updatedTitle != null) {
            updatedTask.setTitle(updatedTitle);
        }
        if (updatedDescription != null) {
            updatedTask.setDescription(updatedDescription);
        }
        updatedTask.setUpdateDate(localDateTime);
        return tasks.replace(task.getID(), task, updatedTask);
    }

    @Override
    public boolean deleteTask(Task task) {
        return tasks.remove(task.getID(), task);
    }
}
