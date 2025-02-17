package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import utils.CSVTaskFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// Создаем собственное исключение
class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}

// менеджжер задач с сохранением в файл
public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Ошибка создания файла для работы со списком задач"
                        + file.getName() + ": " + e.getMessage());
            }
        }
    }

    public static FileBackedTaskManager loadFromFile (File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            // на случай певрого запуска или когда указанный файл не найден,
            // предупредим пользователя и бюудем рабоать без восстановления
            //throw new RuntimeException("Файл " + file.getName() + " не существует. Работа программы прекращена.");
            System.out.println("Файл " + file.getName() + " не найден. Программа запущена без восстановления данных.");
        }
        if (file.length() > 0) {
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                if (lines.size() < 3) {
                    throw new RuntimeException("Файл " + file.getName() + " содержит некорректные данные. Работа программы прекращена.");
                }
                int maxUsedId = 0;
                int i = 1;
                while (i < lines.size()) {
                    if (lines.get(i).isBlank() || lines.get(i).isEmpty()) {
                        i++;
                        continue;
                    }
                    if (lines.get(i).startsWith("history:")) {
                        ArrayList<Integer> historyFromFile = CSVTaskFormat.historyFromString(lines.get(i));
                        // восстанавливаем историю, заправшивая объекты в порядке прихода id
                        for (Integer itemId : historyFromFile) {
                            if (taskManager.getTaskTypeById(itemId) == TaskType.TASK) {
                                Task t = taskManager.getTask(itemId);
                            } else if (taskManager.getTaskTypeById(itemId) == TaskType.EPIC) {
                                Epic e = taskManager.getEpic(itemId);
                            } else if (taskManager.getTaskTypeById(itemId) == TaskType.SUBTASK) {
                                Subtask s = taskManager.getSubtask(itemId);
                            }
                        }

                    } else {
                        Task taskFromFile = CSVTaskFormat.taskFromString(lines.get(i));
                        if (taskFromFile != null) {
                            if (maxUsedId < taskFromFile.getId()) {
                                maxUsedId = taskFromFile.getId();
                            }
                            if (taskFromFile.getTaskType() == TaskType.TASK) {
                                taskManager.addRestoredTask(taskFromFile);
                            } else if (taskFromFile.getTaskType() == TaskType.EPIC) {
                                taskManager.addRestoredEpic((Epic) taskFromFile);
                            } else if (taskFromFile.getTaskType() == TaskType.SUBTASK) {
                                taskManager.addRestoredSubtask((Subtask) taskFromFile);
                            } else {
                                throw new RuntimeException("Из файла считана задача неопределенного типа: id = "
                                        + taskFromFile.getTaskType() + ", строка файла = " + i);
                            }
                        }
                    }
                    i++;
                }
                taskManager.setActualId(++maxUsedId);

            } catch (IOException e) {
                System.out.println("Произошла ошибка во время чтения файла "+ file.getName());
            }
        }

        return taskManager;
    }

    protected void save() throws ManagerSaveException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task t : getTaskList()) {
                writer.write(CSVTaskFormat.toString(t));
                writer.newLine();
            }
            for (Epic e : getEpicList()) {
               writer.write(CSVTaskFormat.toString(e));
               writer.newLine();
            }

            for (Subtask s : getSubtaskList()) {
                writer.write(CSVTaskFormat.toString(s));
                writer.newLine();
            }
            writer.newLine();
            writer.write(CSVTaskFormat.toString(getHistory()));
        } catch (IOException e) {
            //System.out.println("Произошла ошибка во время записи в файл.");
            throw new ManagerSaveException("Произошла ошибка во время записи в файл: " + e.getMessage());
        }
    }

    // блок работы с задачами (Task)
    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public int addTask (Task task) {
        final int id = super.addTask(task);
        save();
        return id;
    }

    // методы addRestored*** - для записи восстановленных задач в память.
    // если использовать обычные методы add***, то каждый раз будет дергаться запись в файл.
    // А зачем это надо?
    public void addRestoredTask (Task task) {
        final int id = super.addTask(task);
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    // блок работы с эпиками (Epic)
    @Override
    public Epic getEpic(int epicId) {
        final Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public int addEpic(Epic epic) {
        final int id = super.addEpic(epic);
        save();
        return id;
    }

    public void addRestoredEpic (Epic epic) {
        final int id = super.addEpic(epic);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void deleteEpic(Epic e) {
        super.deleteEpic(e);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    // блок работы с подзадачами (Subtask)
    @Override
    public Subtask getSubtask(int subtaskId) {
        final Subtask subtask = super.getSubtask(subtaskId);
        save();
        return subtask;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        final int id = super.addSubtask(subtask);
        save();
        return id;
    }

    public void addRestoredSubtask (Subtask subtask) {
        final int id = super.addSubtask(subtask);
    }

    @Override
    public int updateSubtask(Subtask newSubtask) {
        final int result = super.updateSubtask(newSubtask);
        save();
        return result;
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic e) {
        super.deleteAllSubtasksByEpic(e);
        save();
    }

}
