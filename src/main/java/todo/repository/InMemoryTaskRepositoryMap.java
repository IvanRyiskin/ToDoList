package todo.repository;

import todo.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskRepositoryMap implements TaskRepository<Task> {
    Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public boolean addTask(Task task) {
        return tasks.put(task.getID(), task) == null;
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
    public boolean updateTask(int id, String updatedTitle, String updatedDescription) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            if (updatedTitle != null) {
                task.setTitle(updatedTitle);
            }
            if (updatedDescription != null) {
                task.setDescription(updatedDescription);
            }
            task.setUpdateDate();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(int id) {
        return tasks.remove(id) != null;
    }
}
