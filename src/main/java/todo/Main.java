package todo;

import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileRepository;
import todo.repository.FileTaskRepository;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;
import todo.service.FileWorker;
import todo.service.TaskService;
import todo.view.ConsoleView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // Блокирующая очередь для передачи меток
        BlockingQueue<FileTask> blockingQueue = new LinkedBlockingQueue<>();

        // Файловый репозиторий (для хранения в файле ОС)
        FileRepository fileRepository = new FileTaskRepository();

        // Репозиторий (хранилище в памяти)
        ConcurrentMap<Integer, Task> taskFileRepository = fileRepository.getData();
        TaskRepository<Task> inMemoryRepository = new InMemoryTaskRepositoryMap(taskFileRepository);

        // Сервис, использующий репозиторий
        TaskService service = new TaskService(inMemoryRepository, blockingQueue);

        // 2-ой поток, обрабатывающий файл
        FileWorker fileWorker = new FileWorker(fileRepository, blockingQueue, inMemoryRepository);
        fileWorker.start();

        // Консольный интерфейс
        ConsoleView view = new ConsoleView(service, blockingQueue);
        view.start();
    }
}

// Main запускает 2 поток с FileWorker
// ConsoleView работает с FileWorker через blockingQueue (отправляет задачи  по изменению файла) он же отправляет сигнал для завершения потока (в методе старт вызывается метод стоп)
// TaskService работает с FileWorker через blockingQueue (отправляет задачи  по изменению мапы)

// В ConsoleView написать методы по изменению файла с директорией и завершению работы (все передается в blockingQueue)
// В TaskService передавать в blockingQueue команду на обновления файла
// В FileWorker написать методы по работе с файлом (вытаскивать значение и по значению определять какой метод вызывать)
// Метод закрытия так же вызывать внутри треда