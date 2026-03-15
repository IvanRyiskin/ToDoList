package todo.service;

import todo.model.FileTask;
import todo.repository.FileTaskRepository;
import todo.repository.InMemoryTaskRepositoryMap;

import java.util.concurrent.*;

public class FileWorker {
    final FileTaskRepository fileTaskRepository;
    final BlockingQueue<FileTask> blockingQueue;
    final InMemoryTaskRepositoryMap inMemoryTaskRepositoryMap;
    private Future<?> future;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public FileWorker(FileTaskRepository fileTaskRepository,
                      BlockingQueue<FileTask> blockingQueue,
                      InMemoryTaskRepositoryMap inMemoryTaskRepositoryMap) {
        this.fileTaskRepository = fileTaskRepository;
        this.blockingQueue = blockingQueue;
        this.inMemoryTaskRepositoryMap = inMemoryTaskRepositoryMap;
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
