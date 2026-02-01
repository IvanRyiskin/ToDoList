package todo.view;

import todo.model.Task;
import todo.service.TaskService;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final TaskService taskService;
    private final Scanner scanner;

    public ConsoleView(TaskService taskService) {
        this.taskService = taskService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
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
                case "0":
                    return;
                default:
                    System.out.println("Введите корректный номер действия!");
            }
        }
    }

    public void printMenu() {
        System.out.println("\n=== Менеджер задач ===");
        System.out.println("Выберите действие:");
        System.out.println("1. Добавить задачу"); // протестировано
        System.out.println("2. Показать задачу по ID"); // протестировано
        System.out.println("3. Показать задачу по названию"); // протестировано
        System.out.println("4. Показать все задачи"); // протестировано
        System.out.println("5. Обновить задачу"); // протестировано
        System.out.println("6. Удалить задачу по ID"); // не протестировано
        System.out.println("0. Выход"); // протестировано
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

    public void addTask() {
        String title;
        String description;

        do {
            System.out.println("Введите название задачи: ");
            title = scanner.nextLine();
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

            try {
                if (Integer.parseInt(input) == 0) {
                    System.out.println("Поиск задачи отменен!");
                    return;
                }
            } catch (NumberFormatException _) {
                // если не число, то продолжаем дальше
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
                        boolean completed = taskService.updateTask(id, title, description);
                        if (!completed) {
                            printFailedUpdateMessage.run();
                            continue;
                        }
                        break;
                    } else if (!title.isBlank() && description.isBlank()) {
                        boolean completed = taskService.updateTask(id, title, null);
                        if (!completed) {
                            printFailedUpdateMessage.run();
                            continue;
                        }
                        break;
                    } else if (title.isBlank() && !description.isBlank()) {
                        boolean completed = taskService.updateTask(id, null, description);
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
                        boolean completeDelete = taskService.deleteTask(id);
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
}

