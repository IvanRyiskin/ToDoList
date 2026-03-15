package todo;

import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileTaskRepository;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;
import todo.service.TaskService;
import todo.view.ConsoleView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // Блокирующая очередь для передачи меток
        BlockingQueue<FileTask> blockingQueue = new LinkedBlockingQueue<>();

        // Файловый репощиторий (для хранения в файле ОС)
        FileTaskRepository fileReposytory = new FileTaskRepository();

        // Репозиторий (хранилище в памяти)
        TaskRepository<Task> repository = new InMemoryTaskRepositoryMap();

        // Сервис, использующий репозиторий
        TaskService service = new TaskService(repository, blockingQueue);

        // Консольный интерфейс
        ConsoleView view = new ConsoleView(service, blockingQueue);
        view.start();
    }
}

// Main запускает 2 поток с FileWorker
// ConsoleView работает с FileWorker через blockingQueue (отправляет задачи  по изменению файла) он же отправляет сигнал для завершения потока (в методе старт вызывается метод стоп)
// TaskService работает с FileWorker через blockingQueue (отправляет задачи  по изменению мапы)