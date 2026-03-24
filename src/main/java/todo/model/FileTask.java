package todo.model;

import java.nio.file.Path;

public record FileTask(FileAction action, Path path) {
}
