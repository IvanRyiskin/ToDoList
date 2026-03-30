package todo.view;

import todo.model.FileTask;
import todo.model.Task;
import todo.service.FileOperationCallback;
import todo.service.TaskService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import static todo.model.FileAction.CHANGE_FILE;
import static todo.model.FileAction.EXIT;

public class ConsoleView implements FileOperationCallback {
    private final TaskService taskService;
    private final BlockingQueue<FileTask> blockingQueue;
    private final Scanner scanner;

    public ConsoleView(TaskService taskService, BlockingQueue<FileTask> blockingQueue) {
        this.taskService = taskService;
        this.blockingQueue = blockingQueue;
        this.scanner = new Scanner(System.in);
    }

    // CallBack реализация от FileWorker
    public void addToFile(String taskName) {
        synchronized (System.out) {
            System.out.println("\nЗадача " + taskName + " была добавлена в файл");
        }
    }

    public void updateToFile(String taskName) {
        synchronized (System.out) {
            System.out.println("\nЗадача " + taskName + " была обновлена в файле");
        }
    }

    public void deleteFromFile(String taskName) {
        synchronized (System.out) {
            System.out.println("\nЗадача " + taskName + " была удалена из файла");
        }
    }

    public void changedFilePath(Path path) {
        synchronized (System.out) {
            System.out.println("\nФайловый путь был изменен на " + path.getParent() + ", " + "с именем файла: " + path.getFileName());
        }
    }

    public void errorSaveToFile(String taskName) {
        synchronized (System.out) {
            System.out.println("\nОшибка сохранения задачи " + taskName + " в файл");
        }
    }

    public void errorChangeFilePath(Path path) {
        synchronized (System.out) {
            System.out.println("\nОшибка изменения файлогово пути на " + path.getParent());
        }
    }

    public void start() {
        while (true) { // в будущем сделать проверку на интеррапт и обработку, если что-то пойдет не так в самом цикле
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addTask();
                    continue;
                case "2":
                    getTaskById();
                    continue;
                case "3":
                    getTaskByTitle();
                    continue;
                case "4":
                    getAllTasks();
                    continue;
                case "5":
                    updateTask();
                    continue;
                case "6":
                    deleteTask();
                    continue;
                case "7":
                    changeFilePath();
                    continue;
                case "0":
                    try {
                        blockingQueue.put(new FileTask(EXIT));
                    } catch (InterruptedException e) {
                        // Подумать над экстренным сохранением в файл всех данных
                        System.err.println("Произошло непредвиденное завершение работы программы. Завершаем все процессы...");
                        throw new RuntimeException(e);
                    }
                    return;
                default:
                    System.out.println("Введите корректный номер действия!");
            }
        }
    }

    public void printMenu() {
        System.out.println("\n=== Менеджер задач ===");
        System.out.println("Выберите действие:");
        System.out.println("1. Добавить задачу");
        System.out.println("2. Показать задачу по ID");
        System.out.println("3. Показать задачу по названию");
        System.out.println("4. Показать все задачи");
        System.out.println("5. Обновить задачу");
        System.out.println("6. Удалить задачу по ID");
        System.out.println("7. Изменить расположение файла; создать новый в новом месте");
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

    private void checkTitle(String title) {
        if (title.isBlank()) {
            System.out.println("Название не может быть пустым!");
        }
    }

    private void checkDescription(String description) {
        if (description.isBlank()) {
            System.out.println("Описание не может быть пустым!");
        }
    }

    private boolean checkDeletionAgreement(int id) {
        String answer;

        System.out.println("Вы уверены, что хотите удалить задачу с ID " + id + "? (y(yes)/n(no))");

        while (true) {
            answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return true;
            } else if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                return false;
            } else {
                System.out.println("Введите y(yes) или n(no)");
            }
        }
    }

    private boolean checkIntegerInput(String input) {
        if (input.isBlank()) {
            System.out.println("Введена пустая строка. Введите корректный числовой ID или 0 для выхода.");
            return false;
        } else if (!input.strip().matches("\\d+")) {
            System.out.println("Некорректный ввод, ожидается целое положительное число. Попробуйте снова или введите 0 для выхода.");
            return false;
        } else {
            return true;
        }
    }

    private boolean checkExitCommand(String input) {
        try {
            if (Integer.parseInt(input) == 0) {
                return true;
            }
        } catch (NumberFormatException _) {
            // если не 0, то продолжаем дальше
            return false;
        }
        return false;
    }

    public void addTask() {
        String title;
        String description;

        do {
            System.out.println("Введите название задачи: ");
            title = scanner.nextLine();
            // Проверка на команду 0
            if (checkExitCommand(title)) {
                System.out.println("Добавление новой задачи отменено!");
                return;
            }
            checkTitle(title);
        } while (title.isBlank());

        do {
            System.out.println("Введите описание задачи: ");
            description = scanner.nextLine();
            checkDescription(description);
        } while (description.isBlank());

        Task task = taskService.createTask(title, description);
        boolean successAdded = taskService.addTask(task);
        if (successAdded) {
            System.out.println("Задача добавлена успешно!");
        } else {
            System.out.println("Не удалось добавить задачу");
        }
    }

    public void getTaskById() {
        int id;
        Task task;
        String input;

        while (true) {
            System.out.println("Введите ID задачи для поиска: ");
            input = scanner.nextLine();

            if (checkIntegerInput(input)) {
                id = Integer.parseInt(input.strip());
                if (id > 0 && (task = taskService.getTask(id)) != null) {
                    System.out.println("Задача найдена: " + task + "\n");
                    return;
                } else if (id > 0 && taskService.getTask(id) == null) {
                    System.out.println("Задача с ID " + id + " не найдена. Попробуйте снова или введите 0 для выхода");
                } else if (id < 0) {
                    System.out.println("ID не может быть отрицательным");
                } else if (id == 0) {
                    System.out.println("Поиск задачи отменен!");
                    return;
                }
            }
        }
    }

    public void getTaskByTitle() {
        String input;
        Task task;

        while (true) {
            System.out.println("Введите заголовок задачи для поиска: ");
            input = scanner.nextLine();

            // Проверка на команду 0
            if (checkExitCommand(input)) {
                System.out.println("Поиск задачи отменен!");
                return;
            }

            if (input.isBlank()) {
                System.out.println("Введена пустая строка. Введите корректный числовой ID или 0 для выхода.");
            } else {
                if ((task = taskService.getTask(input)) != null) {
                    System.out.println("Задача найдена: " + task + "\n");
                    return;
                } else if ((taskService.getTask(input)) == null) {
                    System.out.println("Задача с заголовком " + input + " не найдена. Попробуйте снова или введите 0 для выхода");
                }
            }
        }
    }

    public void getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        for (Task task : taskService.getAllTasks()) {
            System.out.println(task);
        }
    }

    public void updateTask() {
        int id;
        Task task;
        String title;
        String description;
        Runnable printFailedUpdateMessage = () -> System.out.println("Не удалось обновить задачу, попробуйте снова или введите 0 для выхода");

        while (true) {
            System.out.println("Введите ID задачи для обновления: ");
            String input = scanner.nextLine();
            if (checkIntegerInput(input)) {
                id = Integer.parseInt(input.strip());
                task = taskService.getTask(id);
                if (id > 0 && task != null) {
                    System.out.println("Введите новое название задачи: ");
                    title = scanner.nextLine();
                    System.out.println("Введите новое описание задачи: ");
                    description = scanner.nextLine();
                    if (!title.isBlank() && !description.isBlank()) {
                        boolean completed = taskService.updateTask(task, title, description);
                        if (!completed) {
                            printFailedUpdateMessage.run();
                            continue;
                        }
                        break;
                    } else if (!title.isBlank() && description.isBlank()) {
                        boolean completed = taskService.updateTask(task, title, null);
                        if (!completed) {
                            printFailedUpdateMessage.run();
                            continue;
                        }
                        break;
                    } else if (title.isBlank() && !description.isBlank()) {
                        boolean completed = taskService.updateTask(task, null, description);
                        if (!completed) {
                            printFailedUpdateMessage.run();
                            continue;
                        }
                        break;
                    }
                } else if (id > 0) {
                    System.out.println("Задача с ID " + id + " не найдена. Попробуйте снова или введите 0 для выхода");
                } else if (id == 0) {
                    System.out.println("Обновление задачи отменено!");
                    return;
                }
            }
        }
        System.out.println("Задача успешно обновлена! \n" + task);
    }

    public void deleteTask() {
        int id;
        Task task;
        String input;

        while (true) {
            System.out.println("Введите ID задачи для удаления: ");
            input = scanner.nextLine();
            if (checkIntegerInput(input)) {
                id = Integer.parseInt(input.strip());
                task = taskService.getTask(id);
                if (id > 0 && task != null) {
                    if (checkDeletionAgreement(id)) {
                        boolean completeDelete = taskService.deleteTask(task);
                        if (!completeDelete) {
                            System.out.println("Не удалось удалить задачу, попробуйте снова или введите 0 для выхода");
                            continue;
                        }
                    } else {
                        System.out.println("Удаление задачи " + id + " отменено!");
                        continue;
                    }
                    break;
                } else if (id > 0) {
                    System.out.println("Задача с ID " + id + " не найдена. Попробуйте снова или введите 0 для выхода");
                } else if (id < 0) {
                    System.out.println("ID не может быть отрицательным");
                } else {
                    System.out.println("Удаление задачи отменено!");
                    return;
                }
            }
        }
        System.out.println("Задача успешно удалена!");
    }

    public void changeFilePath() {
        String input;

        while (true) {
            System.out.println("Введите новый путь до файла. Формат: dir1,dir2,...,file.csv");
            input = scanner.nextLine();

            // Проверка на команду 0
            if (checkExitCommand(input)) {
                System.out.println("Изменение пути отменено!");
                return;
            }

            if (isValidPathInput(input)) {
                break;
            } else {
                System.out.println("Некорректный формат пути. Пример: data,tasks,todo.csv");
                System.out.println("Убедитесь, что:");
                System.out.println("- части разделены запятыми");
                System.out.println("- нет пустых названий");
                System.out.println("- нет недопустимых символов");
                System.out.println("- последняя часть — имя файла с расширением");
            }
        }

        Path path = makePath(input.trim().split(","));
        try {
            blockingQueue.put(new FileTask(CHANGE_FILE, path));
        } catch (InterruptedException e) {
            try {
                blockingQueue.put(new FileTask(EXIT));
            } catch (InterruptedException ex) {
                // Подумать над экстренным сохранением в файл всех данных
                System.err.println("Произошло непредвиденное завершение работы программы. Завершаем все процессы...");
                throw new RuntimeException(ex);
            }
        }
    }

    private boolean isValidPathInput(String input) {
        if (input == null || input.trim().isBlank()) {
            return false;
        }
        String[] parts = input.trim().split(",");
        if (parts.length < 1) {
            return false;
        }

        // Регулярное выражение для допустимых имён файлов/директорий
        // Запрещаем: \ / : * ? " < > | и другие недопустимые символы
        String regex = "^[^\\\\/:*?\"<>|]+$";
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isBlank()) {
                return false;
            }
            if (!part.matches(regex)) {
                return false;
            }
        }

        // Проверяем, что последний элемент — это файл (содержит расширение)
        String lastPart = parts[parts.length - 1].trim();

        return isValidFileExtension(lastPart);
    }

    private boolean isValidFileExtension(String fileName) {
        if (!fileName.contains(".")) {
            return false;
        }

        // Список разрешённых расширений
        List<String> allowedExtensions = List.of(".csv", ".txt", ".json", ".xml", ".dat", ".yaml", ".properties");

        // Получаем расширение, включая точку
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        return allowedExtensions.contains(extension);
    }

    private Path makePath(String[] parts) {
        Path result = Path.of(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result = result.resolve(parts[i].trim());
        }
        return result;
    }
}

