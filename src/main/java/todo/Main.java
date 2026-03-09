package todo;

import todo.model.Task;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;
import todo.service.TaskService;
import todo.view.ConsoleView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // Блокирующая очередь для передачи меток
        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

        // Репозиторий (хранилище в памяти)
        TaskRepository<Task> repository = new InMemoryTaskRepositoryMap();

        // Сервис, использующий репозиторий
        TaskService service = new TaskService(repository);

        // Консольный интерфейс
        ConsoleView view = new ConsoleView(service);
        view.start();
    }
}

// Создать класс FileTaskRepository для записи\чтения из файла
// в нем ObjectOutputStream и ObjectInputStream

// Создать класс воркер (2 поток) для чтения блок очереди и работы с FileTaskRepository (сделать через ExecutorService)
// проверять файл, делать чтение или создание файла, если его нет
// там же закрытие потока, если в очереди команда на закрытие