package controllers;

import model.Task;
import model.TaskState;
import org.junit.jupiter.api.*;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.GlobalSettings.DATETIME_FORMAT_PATTERN;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        setTaskManager(new InMemoryTaskManager());
        setHistoryManager(Managers.getDefaultHistory());
    }

    @Test
    @Override
    void getId() {
        super.getId();
    }

    // тесты методов работы с Тасками
    @Test
    @Override
    void getTaskList() {
        super.getTaskList();
    }

    @Test
    @Override
    void getTask() {
        super.getTask();
    }

    @Test
    @Override
    void addTask() {
        super.addTask();
    }

    @Test
    @Override
    void updateTask() {
        super.updateTask();
    }

    @Test
    @Override
    void deleteTask() {
        super.deleteTask();
    }

    @Test
    @Override
    void deleteAllTasks() {
        super.deleteAllTasks();
    }

    //тесты методов работы с Эпиками
    @Test
    @Override
    void getEpicList() {
        super.getEpicList();
    }

    @Test
    @Override
    void getEpic() {
        super.getEpic();
    }

    @Test
    @Override
    void addEpic() {
        super.addEpic();
    }

    @Test
    @Override
    void updateEpic() {
        super.updateEpic();
    }

    @Test
    @Override
    void deleteEpic() {
        super.deleteEpic();
    }

    @Test
    @Override
    void deleteAllEpics() {
        super.deleteAllEpics();
    }

    //тесты методов работы с Подзадачами
    @Test
    @Override
    void getSubtaskList() {
        super.getSubtaskList();
    }

    @Test
    @Override
    void getSubtask() {
        super.getSubtask();
    }

    @Test
    @Override
    void addSubtask() {
        super.addSubtask();
    }

    @Test
    @Override
    void updateSubtask() {
        super.updateSubtask();
    }

    @Test
    @Override
    void deleteSubtask() {
        super.deleteSubtask();
    }

    @Test
    @Override
    void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Test
    @Override
    void deleteAllSubtasksByEpic() {
        super.deleteAllSubtasksByEpic();
    }

    // тесты прочих методов - получения истории и получения сортированного списка задач
    @Test
    @Override
    void getHistory() {
        super.getHistory();
    }

    @Test
    @Override
    void getPrioritizedTasks() {
        super.getPrioritizedTasks();
    }

    /*
    далее идут тесты на методы, которых нет в интерфейсе TaskManager, но они работают и
    в ИнМемори, и в файловом менеджерах
    */
    @Test
    @Override
    void checkEpicStatusWhenAllSubtaskNew() {
        super.checkEpicStatusWhenAllSubtaskNew();
    }

    @Test
    @Override
    void checkEpicStatusWhenAllSubtaskDone() {
        super.checkEpicStatusWhenAllSubtaskDone();
    }

    @Test
    @Override
    void checkEpicStatusWhenSubtaskNewAndDone() {
        super.checkEpicStatusWhenSubtaskNewAndDone();
    }

    @Test
    @Override
    void checkEpicStatusWhenAllSubtaskInprogress() {
        super.checkEpicStatusWhenAllSubtaskInprogress();
    }

    @Test
    @Override
    void checkEpicTimes() {
        super.checkEpicTimes();
    }

    // тесты для истории
    @Test
    @Override
    void checkEmptyHistory() {
        super.checkEmptyHistory();
    }

    @Test
    @Override
    void checkHistoryDuplicates() {
        super.checkHistoryDuplicates();
    }

    @Test
    @Override
    void checkHistoryDeletions() {
        super.checkHistoryDeletions();
    }

    // тест метода проверки пересечения задач по времени - его нет в интерфейсе TaskManager,
    // он специфичен для класса InMemoryTaskManager
    @Test
    void hasIntersectWithOtherTask () {
        InMemoryTaskManager inMemTaskMgr = new InMemoryTaskManager();

        Task t1 = new Task("Test t1 for hasIntersectWithOtherTask",
                "Test t1 for hasIntersectWithOtherTask description",
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:04:05", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(6));

        inMemTaskMgr.addTask(t1);

        Task t2 = new Task("Test t2 for hasIntersectWithOtherTask",
                "Test t2 for hasIntersectWithOtherTask description",
                TaskState.NEW,
                LocalDateTime.parse("2025-01-02 03:03:03", DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)),
                Duration.ofMinutes(3));
        t2.setId(inMemTaskMgr.getId());

        assertTrue(inMemTaskMgr.hasIntersectWithOtherTask(t2), "В данном тесте задачи должны пересекаться по времени");

    }

}