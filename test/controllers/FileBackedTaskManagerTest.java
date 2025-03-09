package controllers;

import model.Task;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Managers;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.GlobalSettings.FILE_NAME_TEST;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>  {
    @BeforeEach
    void setUp() {
        setTaskManager(new FileBackedTaskManager(new File(FILE_NAME_TEST)));
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

    // далее - тесты на методы, которые есть только у файлового менеджера
    @Test
    void saveTest() {
        FileBackedTaskManager fileTaskMgr = new FileBackedTaskManager(new File(FILE_NAME_TEST));

        Task task1 = new Task("Test task1 for saveTest",
                "Test task1 for saveTest description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task1.setId(fileTaskMgr.getId());
        fileTaskMgr.addRestoredTask(task1);

        Task task2 = new Task("Test task2 for saveTest",
                "Test task2 for saveTest description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task2.setId(fileTaskMgr.getId());
        fileTaskMgr.addRestoredTask(task2);

        fileTaskMgr.save();
        File fileTest = new File(FILE_NAME_TEST);
        assertTrue(fileTest.exists(), "После добавления задача файл должен существовать в файлолвой системе");
        assertTrue(
                (fileTest.length() > (task1.getName().length() + task1.getDescription().length() +
                        task2.getName().length() + task2.getDescription().length())),
                "Размер тестового файла меньше, чем могло быть после записи двух задач"
        );

    }

    @Test
    void loadFromFileTest() {
        FileBackedTaskManager fileTaskMgrSaver = new FileBackedTaskManager(new File(FILE_NAME_TEST));

        Task task1 = new Task("Test task1 for saveTest",
                "Test task1 for saveTest description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task1.setId(fileTaskMgrSaver.getId());
        fileTaskMgrSaver.addRestoredTask(task1);

        Task task2 = new Task("Test task2 for saveTest",
                "Test task2 for saveTest description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task2.setId(fileTaskMgrSaver.getId());
        fileTaskMgrSaver.addRestoredTask(task2);

        fileTaskMgrSaver.save();

        FileBackedTaskManager fileTaskMgrReader = FileBackedTaskManager.loadFromFile(new File(FILE_NAME_TEST));

        assertTrue(
                ((fileTaskMgrReader.getTaskList().size() == 2)
                        && (fileTaskMgrReader.getEpicList().size() == 0)
                        && (fileTaskMgrReader.getSubtaskList().size() == 0)
                        && (fileTaskMgrReader.historyMgr.getHistory().size() == 0)),
                "После чтения файла размеры списка задач, эпиков, подзадач и размер истории не соответствуют ожидаемым значениям");

    }

}
