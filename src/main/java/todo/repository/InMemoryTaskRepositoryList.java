package todo.repository;

import todo.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskRepositoryList implements TaskRepository<Task> {
    List<Task> tasks = new ArrayList<>();

    @Override
    public boolean addTask(Task task) {
        return tasks.add(task);
    }

    @Override
    public Task getTask(int id) {
        for (Task task : tasks) {
            if (task.getID() == id) {
                return task;
            }
        }
        return null;
    }

    @Override
    public Task getTask(String title) {
        for (Task task : tasks) {
            if (task.getTitle().equals(title)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks;
    }

    @Override
    public boolean updateTask(int id, String updatedTitle, String updatedDescription) {
        for (Task task : tasks) {
            if (task.getID() == id) {
                if (updatedTitle != null) {
                    task.setTitle(updatedTitle);
                }
                if (updatedDescription != null) {
                    task.setDescription(updatedDescription);
                }
                task.setUpdateDate();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteTask(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getID() == id) {
                tasks.remove(i);
                return true;
            }
        }
        return false;
    }
}
