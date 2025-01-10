package controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static model.TaskState.NEW;
import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Subtask;
import model.Task;
import utils.Managers;

import java.util.ArrayList;

class TaskManagerTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeAll
    static void prepareTestEnv(){
        taskManager = Managers.getDefaultManager();
        assertNotNull(taskManager, "Объект taskManager не готов к работе");
        historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Объект historyManager не готов к работе");
    }

    @Test
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

}