package todo.service;

import todo.model.FileAction;
import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileRepository;
import todo.repository.TaskRepository;

import java.nio.file.Path;
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
                    FileAction action = task.action();
                    Path path = task.path();
                    fileAction(action, path);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void stop() {
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

    private static Task copyTask(Task task) {
        return new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE());
    }

    private ConcurrentMap<Integer, Task> createSnapshot() {
        ConcurrentMap<Integer, Task> snapshot = new ConcurrentHashMap<>();
        for (Task task : inMemoryTaskRepositoryMap.getAllTasks()) {
            snapshot.put(task.getID(), copyTask(task));
        }
        return snapshot;
    }

    private void fileAction(FileAction action, Path data) {
        switch (action) {
            // придумать как посылать итог ConsoleView
            // как вариант - создать еще один поток, читающий итог этого из blockingQueue и выводящий итог в консоль
            case PUT -> fileTaskRepository.writeData(createSnapshot());
            case CHANGE_FILE -> fileTaskRepository.createFile(data);
            case EXIT -> stop();
        }
    }
}
