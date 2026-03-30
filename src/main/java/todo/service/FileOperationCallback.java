package todo.service;

import java.nio.file.Path;

public interface FileOperationCallback {
    void addToFile(String taskName);

    void updateToFile(String taskName);

    void deleteFromFile(String taskName);

    void changedFilePath(Path path);

    void errorSaveToFile(String taskName);

    void errorChangeFilePath(Path path);
}
