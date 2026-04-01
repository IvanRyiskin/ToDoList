package todo.service;

import todo.model.FileAction;
import todo.model.Task;
import todo.repository.InMemoryTaskRepositoryMap;
import todo.repository.TaskRepository;
import todo.view.ConsoleView;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentMap;

public class ApplicationCoordinator {
    private final ConsoleView view;
    private final TaskService service;

    public ApplicationCoordinator(TaskService service, ConsoleView view) {
        this.view = view;
        this.service = service;
    }

    public void changeInMemoryTaskRepository(ConcurrentMap<Integer, Task> newInMemoryTaskRepository) {
        TaskRepository<Task> inMemoryTaskRepositoryMap = new InMemoryTaskRepositoryMap(newInMemoryTaskRepository);
        service.changeRepository(inMemoryTaskRepositoryMap);
    }

    public void callBackFileAction(FileAction action, Task task) {
        switch (action) {
            case ADD -> view.addToFile(task.getTitle());
            case UPDATE -> view.updateToFile(task.getTitle());
            case DELETE -> view.deleteFromFile(task.getTitle());
        }
    }

    public void callBackPathAction(FileAction action, Path path) {
        switch (action) {
            case CHANGE_FILE -> view.changedFilePath(path);
            case SHOW_PATH -> view.showCurrentPath(path);
        }
    }


    public void callBackErrorSaveToFile(String title) {
        view.errorSaveToFile(title);
    }

    public void callBackErrorChangeFilePath(Path path) {
        view.errorChangeFilePath(path);
    }
}

