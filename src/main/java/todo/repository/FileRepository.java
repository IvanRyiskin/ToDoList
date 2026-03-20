package todo.repository;

import todo.model.Task;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentMap;

public interface FileRepository {

    boolean createFile(Path path);

    ConcurrentMap<Integer, Task> getData();

    boolean writeData(ConcurrentMap<Integer, Task> data);
}
