package controllers;

import model.Task;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyMgr;

    /*
    здесь будут собраны тесты для методов интерфейса HistoryManager.
    тесты для вариантов использования выполнены в классах TaskManagerTest и InMemoryTaskManagerTest.
    Так получилось потому, что в начальных заданиях этого проекта я сделал HistoryManager членом класса
    InMemoryTaskManager, и вся история теперь пишется туда.
     */

    @BeforeEach
    void prepareTestEnv() {
        historyMgr = new InMemoryHistoryManager();
    }


    @Test
    void add() {
        Task task1 = new Task("Test task1 for HistoryMaganer.add",
                "Test task1 for HistoryMaganer.add description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task1.setId(0);
        historyMgr.add(task1);

        Task task2 = new Task("Test task2 for HistoryMaganer.add",
                "Test task2 for HistoryMaganer.add description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task2.setId(1);
        historyMgr.add(task2);

        Task task3 = new Task("Test task3 for HistoryMaganer.add",
                "Test task3 for HistoryMaganer.add description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task3.setId(2);
        historyMgr.add(task3);

        assertTrue((historyMgr.getHistory().size() == 3), "В данном тесте в историю должно быть добавлено три задачи");
    }

    @Test
    void remove() {
        Task task1 = new Task("Test task1 for HistoryMaganer.add",
                "Test task1 for HistoryMaganer.add description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task1.setId(0);
        historyMgr.add(task1);

        Task task2 = new Task("Test task2 for HistoryMaganer.add",
                "Test task2 for HistoryMaganer.add description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task2.setId(1);
        historyMgr.add(task2);

        Task task3 = new Task("Test task3 for HistoryMaganer.add",
                "Test task3 for HistoryMaganer.add description", TaskState.NEW,
                LocalDateTime.now(), Duration.ofMinutes(1));
        task3.setId(2);
        historyMgr.add(task3);

        historyMgr.remove(0);
        historyMgr.remove(2);
        historyMgr.remove(1);

        assertTrue((historyMgr.getHistory().size() == 0), "В данном тесте в истории не должно остаться ни одной задачи");
    }

    @Test
    void getHistory() {
        ArrayList<Task> historyList = historyMgr.getHistory();
        assertNotNull(historyList, "Объект с историей должен быть инициализирован");
        assertTrue((historyList.size() == 0), "В только что созданном объекте истории не должно быть записей");
    }

}
