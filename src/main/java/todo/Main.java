package todo;

import todo.model.Task;
import todo.repository.InMemoryTaskRepository;
import todo.repository.TaskRepository;
import todo.service.TaskService;
import todo.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        // Репозиторий (хранилище в памяти)
        TaskRepository<Task> repository = new InMemoryTaskRepository();

        // Сервис, использующий репозиторий
        TaskService service = new TaskService(repository);

        // Консольный интерфейс
        ConsoleView view = new ConsoleView(service);
        view.start();
    }
}

// Задачи:
// 1. добавить lombok;
// 2. Добавить реализацию с Map