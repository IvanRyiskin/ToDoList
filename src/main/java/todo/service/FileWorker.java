package todo.service;

import todo.model.FileAction;
import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileTaskRepository;
import todo.repository.TaskRepository;

import java.nio.file.Path;
import java.util.concurrent.*;

public class FileWorker {
    final FileTaskRepository fileTaskRepository;
    final BlockingQueue<FileTask> blockingQueue;
    final ApplicationCoordinator applicationCoordinator;
    TaskRepository<Task> inMemoryTaskRepository;
    private Future<?> future;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public FileWorker(FileTaskRepository fileRepository,
                      BlockingQueue<FileTask> blockingQueue,
                      TaskRepository<Task> inMemoryRepositoryMap,
                      ApplicationCoordinator applicationCoordinator) {
        this.fileTaskRepository = fileRepository;
        this.blockingQueue = blockingQueue;
        this.inMemoryTaskRepository = inMemoryRepositoryMap;
        this.applicationCoordinator = applicationCoordinator;
    }

    public void start() {
        future = executor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    FileTask task = blockingQueue.take();
                    FileAction action = task.action();
                    Object[] data = task.data();
                    fileAction(action, data);
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
        return new Task(task.getID(), task.getTitle(), task.getDescription(), task.getCREATEDATE(), task.getUpdateDate());
    }

    private ConcurrentMap<Integer, Task> createSnapshot() {
        ConcurrentMap<Integer, Task> snapshot = new ConcurrentHashMap<>();
        for (Task task : inMemoryTaskRepository.getAllTasks()) {
            snapshot.put(task.getID(), copyTask(task));
        }
        return snapshot;
    }

    private void saveToFile(FileAction action, Task task) {
        try {
            fileTaskRepository.writeData(createSnapshot());
            applicationCoordinator.callBackFileAction(action, task);
        } catch (RuntimeException e) {
            applicationCoordinator.callBackErrorSaveToFile(task.getTitle());
        }
    }

    private void changeFilePath(FileAction action, Path path) {
        try {
            // Обновляем путь
            fileTaskRepository.createFile(path);

            // Обновляем мапу
            ConcurrentMap<Integer, Task> newData = fileTaskRepository.getData();
            applicationCoordinator.changeInMemoryTaskRepository(newData);
            applicationCoordinator.callBackPathAction(action, path);
        } catch (RuntimeException e) {
            applicationCoordinator.callBackErrorChangeFilePath(path);
        }
    }

    private void showFilePath(FileAction action) {
        applicationCoordinator.callBackPathAction(action, fileTaskRepository.getPath());
    }

    private void fileAction(FileAction action, Object[] data) {
        switch (action) {
            case ADD, UPDATE, DELETE -> saveToFile(action, (Task) data[0]);
            case CHANGE_FILE -> changeFilePath(action, (Path) data[0]);
            case SHOW_PATH -> showFilePath(action);
            case EXIT -> stop();
        }
    }
}
