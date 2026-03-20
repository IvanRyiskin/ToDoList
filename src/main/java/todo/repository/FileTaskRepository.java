package todo.repository;

import todo.model.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileTaskRepository implements FileRepository {

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
        // 1. Проверяем, существует ли файл
        if (!Files.exists(path)) {
            return new ConcurrentHashMap<>();
        }

        // 2. Проверяем, пустой ли файл
        long size;
        try {
            size = Files.size(path);
        } catch (IOException e) {
            System.out.println("Не удалось прочитать размер файла: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
        if (size == 0) {
            return new ConcurrentHashMap<>();
        }

        // 3. Читаем объект
        try (ObjectInputStream object = new ObjectInputStream(Files.newInputStream(path))) {
            Object fileObject = object.readObject();
            if (!(fileObject instanceof ConcurrentMap<?, ?>)) {
                System.out.println("Обнаружен неверный формат данных. Создаем новый файл...");
                return new ConcurrentHashMap<>();
            }
            return (ConcurrentMap<Integer, Task>) fileObject;
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