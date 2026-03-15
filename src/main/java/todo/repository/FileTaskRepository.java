package todo.repository;

import todo.model.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileTaskRepository {

    private Path path;

    public FileTaskRepository(Path path) {
        createFile(path);
    }

    public FileTaskRepository() {
        this(Path.of(System.getProperty("user.home"), "TODO.txt"));
    }

    public boolean createFile(Path path) {
        this.path = path;
        if (!Files.exists(path)) {
            try {
                Path parent = this.path.getParent();
                if (parent != null) {
                    Files.createDirectories(parent); // Создаём родительскую папку (если нужно)
                }
                Files.createFile(this.path);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка создания файла по пути" + path);
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public ConcurrentMap<Integer, Task> getData() {
        if (!Files.exists(path)) {
            return new ConcurrentHashMap<>();
        }
        try (ObjectInputStream object = new ObjectInputStream(Files.newInputStream(path))) {
            Object obj = object.readObject();
            if (!(obj instanceof ConcurrentMap<?, ?>)) {
                throw new IllegalStateException("Ожидался ConcurrentMap, но получен: " + obj.getClass());
            }
            return (ConcurrentMap<Integer, Task>) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при чтении данных из файла: " + path, e);
        }
    }

    public boolean writeData(ConcurrentMap<Integer, Task> data) {
        if (!Files.exists(path)) {
            createFile(path);
        }
        try (ObjectOutputStream object = new ObjectOutputStream(Files.newOutputStream(path))) {
            object.writeObject(data);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи данных в файл: " + path, e);
        }
    }
}