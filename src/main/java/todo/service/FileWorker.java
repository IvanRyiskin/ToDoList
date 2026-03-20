package todo.service;

import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileRepository;
import todo.repository.TaskRepository;

import java.util.concurrent.*;

public class FileWorker {
    final FileRepository fileTaskRepository;
    final BlockingQueue<FileTask> blockingQueue;
    final TaskRepository<Task> inMemoryTaskRepositoryMap;
    private Future<?> future;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public FileWorker(FileRepository fileRepository,
                      BlockingQueue<FileTask> blockingQueue,
                      TaskRepository<Task> inMemoryRepositoryMap) {
        this.fileTaskRepository = fileRepository;
        this.blockingQueue = blockingQueue;
        this.inMemoryTaskRepositoryMap = inMemoryRepositoryMap;
    }

    public void start() {
        future = executor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    FileTask task = blockingQueue.take();
                    // логика обработки действий
                    // смотрим у таски тип fileaction, вызываем нужные методы (объявить наравне со старт\стоп методами)
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
