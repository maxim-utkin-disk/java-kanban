package controllers;

import org.junit.jupiter.api.*;

import static model.TaskState.NEW;
import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Subtask;
import model.Task;
import utils.Managers;

import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class TaskManagerTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeAll
    @Order(0)
    static void prepareTestEnv(){
        taskManager = Managers.getDefaultManager();
        assertNotNull(taskManager, "Объект taskManager не готов к работе");
        assertEquals(0, taskManager.getTaskList().size(),
                "В только что созданном объекте taskManager непустой список задач");
        assertEquals(0, taskManager.getSubtaskList().size(),
                "В только что созданном объекте taskManager непустой список подзадач");
        assertEquals(0, taskManager.getEpicList().size(),
                "В только что созданном объекте taskManager непустой список эпиков");

        historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Объект historyManager не готов к работе");
        assertEquals(0, historyManager.getHistory().size(),
                "В только что созданном объекте historyManager непустой список просмотров");
    }

    @Test
    @Order(1)
    void addTask() {
        Task task = new Task("Test addTask", "Test addTask description", NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        assertNotEquals(0,
                taskManager.getHistory().size(),
                "После операций с задачами (Task) история не должна быть пустой!");
    }

    @Test
    @Order(2)
    void addEpic() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        final int epicId = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        assertNotEquals(0,
                taskManager.getHistory().size(),
                "После операций с эпиками история не должна быть пустой!");
    }

    @Test
    @Order(3)
    void addSubtask() {
        final ArrayList<Epic> epics = taskManager.getEpicList();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");

        Subtask subtask = new Subtask("Test addSubtask",
                "Test addSubtask description",
                epics.get(0).getId(),
                NEW
                );
        final int subtaskId = taskManager.addSubtask(subtask);
        assertTrue(subtaskId >= 0, "Подзадача не добавлена");

        final ArrayList<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        assertNotEquals(0,
                taskManager.getHistory().size(),
                "После операций с подзадачами (Subtask) история не должна быть пустой!");
    }

    @Test
    @Order(4)
    void allHistorySizeOverThenZero() {
        assertNotEquals(0,
                taskManager.getHistory().size(),
                "После добавления задач, эпиков и подзадач в предыдущих тестах, история не должна быть пустой!");
    }

    @Test
    @Order(5)
    void noRepeatsInHistory() {
        int task2Id = taskManager.addTask(new Task("Test task 2 name", "Test task 2 descr", NEW));
        Task task21 = taskManager.getTask(task2Id);
        Task task22 = taskManager.getTask(task2Id);
        Task task23 = taskManager.getTask(task2Id);

        int taskRepeatCount = 0;
        for (Task t : taskManager.getHistory()) {
            if (t.getId() == task2Id) {taskRepeatCount++;}
        }

        assertTrue((taskRepeatCount == 1),
                "В истории просмотров задача должна быть только один раз!");
    }

    @Test
    @Order(6)
    void noTaskInHistoryAfterRemove() {
        int task3Id = taskManager.addTask(new Task("Test task 3 name", "Test task 3 descr", NEW));
        Task task3 = taskManager.getTask(task3Id);
        taskManager.deleteTask(task3Id);

        int taskRepeatCount = 0;
        for (Task t : taskManager.getHistory()) {
            if (t.getId() == task3Id) {taskRepeatCount++;}
        }

        assertTrue((taskRepeatCount == 0),
                "После удаления задачи ее не должно быть видно в истории!");
    }

}