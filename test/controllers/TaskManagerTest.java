package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static model.TaskState.NEW;
import static org.junit.jupiter.api.Assertions.*;
import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

abstract public class TaskManagerTest  <T extends TaskManager> {

    private T taskManager;
    private HistoryManager historyManager;

    protected void setTaskManager(T taskManager) {
        this.taskManager = taskManager;
    }

    protected void setHistoryManager(HistoryManager historyMgr) {
        this.historyManager = historyMgr;
    }

    // далее будут перечислены методы тестирования всех доступных методоов интерфейса TaskManager
    void getId() {
        assertEquals(0, taskManager.getId());
        assertEquals(1, taskManager.getId());
    }

    // тесты методов работы с Тасками
    void getTaskList() {
        final ArrayList<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Не создался список задач (task)");
        assertEquals(0, tasks.size(), "Неверное количество элементов в пустом списке задач.");
    };

    void getTask() {
        Task task = new Task("Test for getTask", "Test for getTask description", NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Только что добавленная задача не найдена.");
        assertEquals(task, savedTask, "Только что добавленная и исходная задачи не совпадают.");
    }

    void addTask() {
        Task task = new Task("Test for addTask", "Test for addTask description", NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Только что добавленная задача не найдена.");
        assertEquals(task, savedTask, "Только что добавленная и исходная задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(1, tasks.size(), "Неверное количество элементов в списке задач.");
        assertEquals(task, tasks.get(0), "Исходная и полученная из списка задачи не совпадают.");
    }

    void updateTask() {
        Task task = new Task("Test for updateTask", "Test for updateTask description", NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        final int taskId = taskManager.addTask(task);

        task.setName(task.getName() + " updated");
        task.setDescription(task.getDescription() + " updated");
        taskManager.updateTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertTrue(task.getName().equals(savedTask.getName()) && task.getDescription().equals(savedTask.getDescription()));
    }

    void deleteTask() {
        Task task = new Task("Test for deleteTask", "Test for deleteTask description", NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        final int taskId = taskManager.addTask(task);

        taskManager.deleteTask(taskId);
        assertEquals(0, taskManager.getTaskList().size(), "После удаления единственной задачи список должен быть пустым");
    }

    void deleteAllTasks() {
        Task task1 = new Task("Test for deleteAllTasks 1", "Test for deleteAllTasks description 1", NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task1Id = taskManager.addTask(task1);

        Task task2 = new Task("Test for deleteAllTasks 2", "Test for deleteAllTasks description 2", NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task2Id = taskManager.addTask(task2);

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTaskList().size(), "После удаления всех задач список должен быть пустым");
    }

    //тесты методов работы с Эпиками
    void getEpicList() {
        final ArrayList<Epic> epics = taskManager.getEpicList();
        assertNotNull(epics, "Не создался список составных задач (epic)");
        assertEquals(0, epics.size(), "Неверное количество элементов в пустом списке составных задач.");
    };

    void getEpic() {
        Epic epic = new Epic("Test for getEpic", "Test for getEpic description");
        final int epicId = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Только что добавленный эпик не найдена=.");
        assertEquals(epic, savedEpic, "Только что добавленный и исходный эпики не совпадают.");
    }

    void addEpic() {
        Epic epic = new Epic("Test for addEpic", "Test for addEpic description");
        final int epicId = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Только что добавленный эпик не найден.");
        assertEquals(epic, savedEpic, "Только что добавленный и исходный эпики не совпадают.");

        final ArrayList<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Список эпиков не возвращается.");
        assertEquals(1, epics.size(), "Неверное количество элементов в списке эпиков.");
        assertEquals(epic, epics.get(0), "Исходный и полученный из списка эпики не совпадают.");
    }

    void updateEpic() {
        Epic epic = new Epic("Test for updateEpic", "Test for updateEpic description");
        final int epicId = taskManager.addEpic(epic);

        epic.setName(epic.getName() + " updated");
        epic.setDescription(epic.getDescription() + " updated");
        taskManager.updateEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertTrue(epic.getName().equals(savedEpic.getName()) && epic.getDescription().equals(savedEpic.getDescription()));
    }

    void deleteEpic() {
        Epic epic = new Epic("Test for deleteEpic", "Test for deleteEpic description");
        final int epicId = taskManager.addEpic(epic);

        taskManager.deleteEpic(epic);
        assertEquals(0, taskManager.getEpicList().size(),
                "После удаления единственного эпика список должен быть пустым");
    }

    void deleteAllEpics() {
        Epic epic1 = new Epic("Test for deleteAllEpics 1", "Test for deleteAllEpics description 1");
        final int epic1Id = taskManager.addTask(epic1);

        Epic epic2 = new Epic("Test for deleteAllEpics 2", "Test for deleteAllEpics description 2   ");
        final int epic2Id = taskManager.addTask(epic2);

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpicList().size(), "После удаления всех эпиков список должен быть пустым");
    }

    void actualizeEpicExecutionPeriod() {
        Epic epic = new Epic("Test Epic for actualizeEpicExecutionPeriod",
                "Test Epic for actualizeEpicExecutionPeriod description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test Subtask1 for actualizeEpicExecutionPeriod",
                "Test subtask1 for actualizeEpicExecutionPeriod description",
                epicId,
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask1Id = taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test Subtask2 for actualizeEpicExecutionPeriod",
                "Test subtask2 for actualizeEpicExecutionPeriod description",
                epicId,
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask2Id = taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Test Subtask3 for actualizeEpicExecutionPeriod",
                "Test subtask3 for actualizeEpicExecutionPeriod description",
                epicId,
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 05:06:07", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask3Id = taskManager.addSubtask(subtask3);

        epic.actualizeEpicExecutionPeriod();
        assertTrue(
                (epic.getDuration().toMinutes() == 3),
                "В данном тесте продолжительность эпика должна быть 3 минуты" + " " + epic.getDuration().toMinutes());
    }

    //тесты методов работы с Сабтасками
    void getSubtaskList() {
        final ArrayList<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Не создался список подзадач (subtask)");
        assertEquals(0, subtasks.size(), "Неверное количество элементов в пустом списке подзадач.");
    };

    void getSubtask() {
        Epic epic = new Epic("Test Epic for getSubstask", "Test Epic for getSubtask description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask for getSubstask",
                "Test subtask for getSubtask description",
                epicId, TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        final int subtaskId = taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Только что добавленная подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Только что добавленная и исходная подзадачи не совпадают.");
    }

    void addSubtask() {
        // проверить наличие родительского эпика. Если его нет, не добавлять
        Epic epic = new Epic("Test Epic for addSubtask", "Test Epic for addSubtask description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask for addSubtask",
                "Test subtask for addSubtask description",
                epicId, TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        final int subtaskId = taskManager.addSubtask(subtask);
        assertNotEquals(-1, 1, "Подзадача не добавлена, потому что не найден родительский Эпик");

        assertTrue((taskManager.getSubtaskList().size() == 1), "Подзадача не добавлена");
    }

    void updateSubtask() {
        Epic epic = new Epic("Test Epic for updateSubtask", "Test Epic for updateSubtask description");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask for getSubstask",
                "Test subtask for getSubtask description",
                epicId, TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        final int subtaskId = taskManager.addSubtask(subtask);

        subtask.setName(subtask.getName() + " updated");
        subtask.setDescription(subtask.getDescription() + " updated");
        taskManager.updateSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertTrue(subtask.getName().equals(savedSubtask.getName()) && subtask.getDescription().equals(savedSubtask.getDescription()));
    }

    void deleteSubtask() {
        Epic epic = new Epic("Test Epic for deleteSubtask", "Test Epic for deleteSubtask description");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask for deleteSubtask",
                "Test subtask for deleteSubtask description",
                epicId, TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.deleteSubtask(subtaskId);
        assertEquals(0, taskManager.getSubtaskList().size(), "После удаления единственной подзадачи список должен быть пустым");
    }

    void deleteAllSubtasks() {
        Epic epic1 = new Epic("Test Epic1 for deleteAllSubtasks",
                "Test Epic1 for deleteAllSubtasks description");
        final int epic1Id = taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask("Test Subtask11 for deleteAllSubtasks",
                "Test subtask11 for deleteAllSubtasks description",
                epic1Id, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Test Subtask12 for deleteAllSubtasks",
                "Test subtask12 for deleteAllSubtasks description",
                epic1Id, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 05:06:07", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        Epic epic2 = new Epic("Test Epic2 for deleteAllSubtasks",
                "Test Epic2 for deleteAllSubtasks description");
        final int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask21 = new Subtask("Test Subtask21 for deleteAllSubtasks",
                "Test subtask21 for deleteAllSubtasks description",
                epic1Id, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 06:07:08", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask21Id = taskManager.addSubtask(subtask21);

        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getSubtaskList().size(),
                "После удаления всех подзадач список должен быть пустым");

    }

    void deleteAllSubtasksByEpic() {
        Epic epic1 = new Epic("Test Epic1 for deleteAllSubtasksByEpic",
                "Test Epic1 for deleteAllSubtasksByEpic description");
        final int epic1Id = taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask("Test Subtask11 for deleteAllSubtasksByEpic",
                "Test subtask11 for deleteAllSubtasksByEpic description",
                epic1Id, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Test Subtask12 for deleteAllSubtasksByEpic",
                "Test subtask12 for deleteAllSubtasksByEpic description",
                epic1Id, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        Epic epic2 = new Epic("Test Epic2 for deleteAllSubtasksByEpic",
                "Test Epic2 for deleteAllSubtasksByEpic description");
        final int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask21 = new Subtask("Test Subtask21 for deleteAllSubtasksByEpic",
                "Test subtask21 for deleteAllSubtasksByEpic description",
                epic2Id, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 05:06:07", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask21Id = taskManager.addSubtask(subtask21);

        taskManager.deleteAllSubtasksByEpic(epic2);
        assertEquals(0,
                taskManager.getEpic(epic2Id).getSubtasksPerEpic().size(),
                "После удаления всех подзадач одного эпика список его подзадач должен быть пустым");
        assertTrue((taskManager.getSubtaskList().size() == taskManager.getEpic(epic1Id).getSubtasksPerEpic().size()),
                "После удаления всех подзадач одного эпика список оставшихся подзадач других эпиков не должен быть пустым ");

    }

    // тесты на оставшиеся методы интерфейса TaskManager
    void getHistory() {
        final ArrayList<Task> history = taskManager.getHistory();
        assertNotNull(history, "Не создался список истории");
        assertEquals(0, history.size(), "Неверное количество элементов в пустом списке истории.");
    };

    void getPrioritizedTasks() {
        final ArrayList<Task> sortedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(sortedTasks, "Не создался отсортированный список задач");
        assertEquals(0, sortedTasks.size(), "Неверное количество элементов в пустом списке сортированных задач.");
    }


    /*
     далее идут тесты на методы, которых нет в интерфейсе TaskManager, но они работают и
     в ИнМемори, и в файловом менеджерах
     */
    void checkEpicStatusWhenAllSubtaskNew() {
        Epic epic = new Epic("Test Epic1 for checkEpicStatus",
                "Test Epic1 for checkEpicStatus description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask11 = new Subtask("Test Subtask11 for checkEpicStatus",
                "Test subtask11 for checkEpicStatus description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Test Subtask12 for checkEpicStatus",
                "Test subtask12 for checkEpicStatus description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        Subtask subtask13 = new Subtask("Test Subtask13 for checkEpicStatus",
                "Test subtask13 for checkEpicStatus description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 06:07:08", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask13Id = taskManager.addSubtask(subtask13);

        assertTrue(
                (epic.getStatus() == TaskState.NEW),
                "Если у всех подзадач статус = NEW, то и у Эпика статус должен быть = NEW");
    }

    void checkEpicStatusWhenAllSubtaskDone() {
        Epic epic = new Epic("Test Epic1 for checkEpicStatus",
                "Test Epic1 for checkEpicStatus description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask11 = new Subtask("Test Subtask11 for checkEpicStatus",
                "Test subtask11 for checkEpicStatus description",
                epicId, TaskState.DONE,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Test Subtask12 for checkEpicStatus",
                "Test subtask12 for checkEpicStatus description",
                epicId, TaskState.DONE,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        Subtask subtask13 = new Subtask("Test Subtask13 for checkEpicStatus",
                "Test subtask13 for checkEpicStatus description",
                epicId, TaskState.DONE,
                LocalDateTime.parse("2025-01-02 05:06:07", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask13Id = taskManager.addSubtask(subtask13);

        assertTrue(
                (epic.getStatus() == TaskState.DONE),
                "Если у всех подзадач статус = DONE, то и у Эпика статус должен быть = DONE");
    }

    void checkEpicStatusWhenSubtaskNewAndDone() {
        Epic epic = new Epic("Test Epic1 for checkEpicStatus",
                "Test Epic1 for checkEpicStatus description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask11 = new Subtask("Test Subtask11 for checkEpicStatus",
                "Test subtask11 for checkEpicStatus description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Test Subtask12 for checkEpicStatus",
                "Test subtask12 for checkEpicStatus description",
                epicId, TaskState.DONE,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        assertTrue(
                (epic.getStatus() == TaskState.IN_PROGRESS),
                "Когда у части подзадач статус = NEW, а у части = DONE, то у Эпика статус должен быть = IN_PROGRESS");
    }

    void checkEpicStatusWhenAllSubtaskInprogress() {
        Epic epic = new Epic("Test Epic1 for checkEpicStatus",
                "Test Epic1 for checkEpicStatus description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask11 = new Subtask("Test Subtask11 for checkEpicStatus",
                "Test subtask11 for checkEpicStatus description",
                epicId, TaskState.IN_PROGRESS,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Test Subtask12 for checkEpicStatus",
                "Test subtask12 for checkEpicStatus description",
                epicId, TaskState.IN_PROGRESS,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        Subtask subtask13 = new Subtask("Test Subtask13 for checkEpicStatus",
                "Test subtask13 for checkEpicStatus description",
                epicId, TaskState.IN_PROGRESS,
                LocalDateTime.parse("2025-01-02 05:06:07", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int subtask13Id = taskManager.addSubtask(subtask13);

        assertTrue(
                (epic.getStatus() == TaskState.IN_PROGRESS),
                "Если у всех подзадач статус = IN_PROGRESS, то и у Эпика статус должен быть = IN_PROGRESS");
    }

    void checkEpicTimes() {
        Epic epic = new Epic("Test Epic1 for checkEpicTimes",
                "Test Epic1 for checkEpicTimes description");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask11 = new Subtask("Test Subtask11 for checkEpicTimes",
                "Test subtask11 for checkEpicTimes description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2024-11-12 13:14:15", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(16));
        final int subtask11Id = taskManager.addSubtask(subtask11);

        assertTrue(
                epic.getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)).equals("2024-11-12 13:14:15"),
                "Неправильно вычислено время начала эпика");
        assertTrue(
                epic.getEndTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)).equals("2024-11-12 13:30:15"),
                "Неправильно вычислено время окончания эпика");


        Subtask subtask12 = new Subtask("Test Subtask12 for checkEpicTimes",
                "Test subtask12 for checkEpicTimes description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2024-12-13 14:15:16", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(17));
        final int subtask12Id = taskManager.addSubtask(subtask12);

        assertTrue(
                epic.getEndTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)).equals("2024-12-13 14:32:16"),
                "Неправильно вычислено новое время окончания эпика после добавления подзадачи");


        Subtask subtask13 = new Subtask("Test Subtask13 for checkEpicTimes",
                "Test subtask13 for checkEpicTimes description",
                epicId, TaskState.NEW,
                LocalDateTime.parse("2024-10-11 12:13:14", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(15));
        final int subtask13Id = taskManager.addSubtask(subtask13);

        assertTrue(
                epic.getStartTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)).equals("2024-10-11 12:13:14"),
                "Неправильно вычислено новое время начала эпика после добавления подзадачи");

    }

    void addToPrioritizedTasksList() {
        InMemoryTaskManager inMemTaskMgr = new InMemoryTaskManager();

        Task t1 = new Task("Test t1 for hasIntersectWithOtherTask",
                "Test t1 for hasIntersectWithOtherTask description",
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(6));
        int task1Id = inMemTaskMgr.addTask(t1);
        inMemTaskMgr.addToPrioritizedTasksList(t1);
        assertEquals(1,
                inMemTaskMgr.getPrioritizedTasks().size(),
                "В данном тесте в списке приоритетов должна быть одна задача");

        Task t2 = new Task("Test t2 for hasIntersectWithOtherTask",
                "Test t2 for hasIntersectWithOtherTask description",
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(3));
        int task2Id = inMemTaskMgr.addTask(t2);
        inMemTaskMgr.addToPrioritizedTasksList(t2);

        assertEquals(2,
                inMemTaskMgr.getPrioritizedTasks().size(),
                "В данном тесте в списке приоритетов должно быть две задачи");
    }

    // тесты для истории
    void checkEmptyHistory() {
        ArrayList<Task> tasks = taskManager.getHistory();
        assertTrue((tasks.size() == 0), "История просмотров должна быть пустой");
    }

    void checkHistoryDuplicates() {
        Task task1 = new Task("Test task1 for checkHistoryDuplicates",
                "Test task1 for checkHistoryDuplicates description", NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task1Id = taskManager.addTask(task1);

        Task histTask1 = taskManager.getTask(task1Id);
        Task histTask11 = taskManager.getTask(task1Id);
        Task histTask111 = taskManager.getTask(task1Id);
        Task histTask1111 = taskManager.getTask(task1Id);

        ArrayList<Task> tasks = taskManager.getHistory();
        assertTrue((tasks.size() == 1), "В данном тесте история просмотров должна содержать только одну запись");

    }

    void checkHistoryDeletions() {
        Task task1 = new Task("Test task1 for checkHistoryDeletions",
                "Test task1 for checkHistoryDeletions description", TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task1Id = taskManager.addTask(task1);

        Task task2 = new Task("Test task2 for checkHistoryDeletions",
                "Test task2 for checkHistoryDeletions description", TaskState.NEW,
                LocalDateTime.parse("2025-01-02 04:05:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task2Id = taskManager.addTask(task2);

        Task task3 = new Task("Test task3 for checkHistoryDeletions",
                "Test task3 for checkHistoryDeletions description", TaskState.NEW,
                LocalDateTime.parse("2025-01-02 05:06:06", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task3Id = taskManager.addTask(task3);

        Task task4 = new Task("Test task4 for checkHistoryDeletions",
                "Test task4 for checkHistoryDeletions description", TaskState.NEW,
                LocalDateTime.parse("2025-01-02 06:07:08", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task4Id = taskManager.addTask(task4);

        Task task5 = new Task("Test task5 for checkHistoryDeletions",
                "Test task5 for checkHistoryDeletions description", TaskState.NEW,
                LocalDateTime.parse("2025-01-02 07:08:09", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(1));
        final int task5Id = taskManager.addTask(task5);

        Task histTask1 = taskManager.getTask(task1Id);
        Task histTask2 = taskManager.getTask(task2Id);
        Task histTask3 = taskManager.getTask(task3Id);
        Task histTask4 = taskManager.getTask(task4Id);
        Task histTask5 = taskManager.getTask(task5Id);

        ArrayList<Task> tasks5 = taskManager.getHistory();
        assertTrue((tasks5.size() == 5), "В данном тесте история просмотров должна содержать пять запись");

        //eдаление из начала, середины, конца
        taskManager.deleteTask(task1Id);
        taskManager.deleteTask(task3Id);
        taskManager.deleteTask(task5Id);

        ArrayList<Task> tasks2 = taskManager.getHistory();
        assertTrue((tasks2.size() == 2), "В данном тесте история просмотров должна содержать две записи");
        assertTrue(
                (tasks2.contains(task2) && (tasks2.contains(task4))),
                "В данном тесте история содержит задачи, отличающиеся от ожидаемых"
        );
    }

}
