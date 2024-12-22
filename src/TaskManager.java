import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HashMap<Integer, Epic> epicList;

    private static int id;

    TaskManager() {
        id = -1;
        taskList = new HashMap<>();
        subtaskList = new HashMap<>();
        epicList = new HashMap<>();
    }

    public int getId() {
        return ++id;
    }

    // блок работы с задачами (Task)
    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public Task getTask(int taskId) {
        return taskList.get(taskId);
    }

    public void addTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public void updateTask(Task newTask) {
        if (!taskList.containsKey(newTask.getId())) {
            addTask(newTask);
        } else {
            taskList.replace(newTask.getId(), newTask);
        }
    }

    public void deleteTask(int taskId) {
        if (!taskList.containsKey(taskId)) {
            System.out.println("Задачи с id = " + taskId + " нет в списке, удаление не выполнено");
            return;
        }
        taskList.remove(taskId);
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    public void printTasks() {
        if (taskList.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        System.out.println("Список задач (всего " + taskList.size() + " позиций) :");
        for(Task task : taskList.values()) {
            System.out.println(task);
        }
    }

    public void printAll() {
        System.out.println("Полный перечень всех заданий:");
        printTasks();
        printEpics(true);
        printSubtasks();
    }

    // блок работы с Эпиками (Epic)
    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public Epic getEpic(int epicId) {
        return epicList.get(epicId);
    }

    public void addEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    public void updateEpic(Epic newEpic) {
        if (!epicList.containsKey(newEpic.getId())) {
            addEpic(newEpic);
        } else {
            epicList.replace(newEpic.getId(), newEpic);
        }
    }

    public void deleteEpic(int epicId) {
        if (!epicList.containsKey(epicId)) {
            System.out.println("Эпика с id = " + epicId + " нет в списке, удаление не выполнено");
            return;
        }
        getEpic(epicId).clearAllSubtasks(subtaskList);
        epicList.remove(epicId);
    }

    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    public void printEpics(boolean isShortMode) {
        if (epicList.isEmpty()) {
            System.out.println("Список эпиков пуст");
            return;
        }
        System.out.print("Список эпиков (всего " + epicList.size() + " позиций) ");
        if (isShortMode) {
            System.out.println("(краткий формат):");
            for(Epic epic : epicList.values()) {
                System.out.println(epic);
            }
        } else {
            System.out.println("(полный формат):");
            for(Epic epic : epicList.values()) {
                epic.printEpic(subtaskList);
            }
        }
    }

    // блок работы с Подзадачами (Subtask)
    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    public Subtask getSubtask(int subtaskId) {
        return subtaskList.get(subtaskId);
    }

    public void addSubtask(Subtask subtask) {
        if (!epicList.containsKey(subtask.getEpicId())) {
            System.out.println("В списке эпиков id = " + subtask.getEpicId() + "не найден. Подзадача не добавлена!");
        } else {
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            epicList.get(subtask.getEpicId()).actualizeStatus(subtaskList);
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        if (!epicList.containsKey(newSubtask.getEpicId())) {
            System.out.println("Эпик id=" + newSubtask.getEpicId() +
                    " не найден в списке эпиков. Обновление подзадачи не выполнено");
            return;
        }

        if (!subtaskList.containsKey(newSubtask.getId())) {
            addSubtask(newSubtask);
        } else {
            subtaskList.replace(newSubtask.getId(), newSubtask);
        }

        epicList.get(newSubtask.getEpicId()).actualizeStatus(subtaskList);

    }

    public void deleteSubtask(int subtaskId) {
        if (!subtaskList.containsKey(subtaskId)) {
            System.out.println("Подзадачи с id = " + subtaskId + " нет в списке, удаление не выполнено");
            return;
        }
        Epic epic = getEpic(getSubtask(subtaskId).getEpicId());
        epic.deleteSubtaskId(subtaskId);
        subtaskList.remove(subtaskId);
        epic.actualizeStatus(subtaskList);
    }

    public void deleteAllSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.getSubtaskIdsList().clear();
            epic.actualizeStatus(subtaskList);
        }
    }

    public void printSubtasks() {
        if (subtaskList.isEmpty()) {
            System.out.println("Список подзадач пуст");
            return;
        }
        System.out.println("Список подзадач (всего " + subtaskList.size() +
                " позиций; это полный перечень без разбивки по эпикам):");
        for(Subtask subtask : subtaskList.values()) {
            System.out.println(subtask);
        }
    }
}
