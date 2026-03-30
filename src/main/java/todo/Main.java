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

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // Блокирующая очередь для передачи меток
        BlockingQueue<FileTask> blockingQueue = new LinkedBlockingQueue<>();

        // Файловый репозиторий (для хранения в файле ОС)
        Path path = Path.of("src/main/test_resources/tasks.txt");
        FileRepository fileRepository = new FileTaskRepository(path);

        // Репозиторий (хранилище в памяти)
        ConcurrentMap<Integer, Task> taskFileRepository = fileRepository.getData();
        TaskRepository<Task> inMemoryRepository = new InMemoryTaskRepositoryMap(taskFileRepository);

        // Сервис, использующий репозиторий
        TaskService service = new TaskService(inMemoryRepository, blockingQueue);

        // Консольный интерфейс
        ConsoleView view = new ConsoleView(service, blockingQueue);

        // 2-ой поток, обрабатывающий файл
        FileWorker fileWorker = new FileWorker(fileRepository, blockingQueue, inMemoryRepository, service, view);

        // Запуск программы
        fileWorker.start();
        view.start();
    }
}

// Проблема со сменой файлового пути:
// 1. В FileWorker смену репы с мапой сделал поверхностно.
// Решение: подумать над лучшей реализацией (использовать событие, слушателя или перенести логику обновления на уровень выше)
// 2. При завершении\старте нового сеанса, нет сохранения\загрузки текущего пути. Путь захардкожен и программа всегда начинает с установленого пути
// Решение: нужно придумать динамическое определение пути при старте программы и его сохранение при завершении

// В самом конце добавить статусы синхрониации в таски. Чтобы можно было обрабатывать проблемные таски
//    SYNCED,   // сохранено и в кэше, и в файле
//    PENDING,  // только в кэше, ожидает записи в файл
//    ERROR     // ошибка при записи в файл