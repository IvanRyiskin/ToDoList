package todo.service;

import todo.model.FileAction;
import todo.model.FileTask;
import todo.model.Task;
import todo.repository.FileRepository;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;
import todo.view.ConsoleView;

import java.nio.file.Path;
import java.util.concurrent.*;

public class FileWorker {
    final FileRepository fileTaskRepository;
    final BlockingQueue<FileTask> blockingQueue;
    final ConsoleView consoleView;
    final TaskService service;
    TaskRepository<Task> inMemoryTaskRepositoryMap;
    private Future<?> future;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public FileWorker(FileRepository fileRepository,
                      BlockingQueue<FileTask> blockingQueue,
                      TaskRepository<Task> inMemoryRepositoryMap,
                      TaskService service,
                      ConsoleView consoleView) {
        this.fileTaskRepository = fileRepository;
        this.blockingQueue = blockingQueue;
        this.inMemoryTaskRepositoryMap = inMemoryRepositoryMap;
        this.service = service;
        this.consoleView = consoleView;
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
        for (Task task : inMemoryTaskRepositoryMap.getAllTasks()) {
            snapshot.put(task.getID(), copyTask(task));
        }
        return snapshot;
    }

    private void callBackAction(FileAction action, Task task) {
        switch (action) {
            case ADD -> consoleView.addToFile(task.getTitle());
            case UPDATE -> consoleView.updateToFile(task.getTitle());
            case DELETE -> consoleView.deleteFromFile(task.getTitle());
        }
    }

    private void saveToFile(FileAction action, Task task) {
        try {
            fileTaskRepository.writeData(createSnapshot());
            callBackAction(action, task);
        } catch (RuntimeException e) {
            consoleView.errorSaveToFile(task.getTitle());
        }
    }

    private void changeFilePath(Path path) {
        try {
            // Обновляем путь
            fileTaskRepository.createFile(path);

            // Обновляем мапу
            // Сделал поверхностно. Подумать над лучшей реализацией (использовать событие, слушателя или перенести логику обновления на уровень выше)
            ConcurrentMap<Integer, Task> newTaskFileRepository = fileTaskRepository.getData();
            inMemoryTaskRepositoryMap = new InMemoryTaskRepositoryMap(newTaskFileRepository);
            service.changeRepository(inMemoryTaskRepositoryMap);

            consoleView.changedFilePath(path);
        } catch (RuntimeException e) {
            consoleView.errorChangeFilePath(path);
        }
    }

    private void fileAction(FileAction action, Object[] data) {
        switch (action) {
            case ADD, UPDATE, DELETE -> saveToFile(action, (Task) data[0]);
            case CHANGE_FILE -> changeFilePath((Path) data[0]);
            case EXIT -> stop();
        }
    }
}
