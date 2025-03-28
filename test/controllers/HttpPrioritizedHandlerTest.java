package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;
import utils.Managers;
import utils.TaskListToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpPrioritizedHandlerTest {

    static TaskManager taskManager = Managers.getDefaultManager();
    static HttpTaskServer hts;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpPrioritizedHandlerTest() throws IOException {
    }

    @BeforeAll
    public static void startHttpServer() {
        try {
            hts = new HttpTaskServer(taskManager);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        hts.httpTaskServerStart();
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

    @AfterAll
    public static void shutDown() {
        hts.httpTaskServerStop();
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Testing task",
                TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int taskId = taskManager.addTask(task);

        Epic epic = new Epic("Test epic 1", "Testing epic 1");
        int epicId = taskManager.addEpic(epic);

        Subtask s1 = new Subtask("Test subtask 1", "Testing subtask 1", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        int subtask1Id = taskManager.addSubtask(s1);

        Subtask s2 = new Subtask("Test subtask 2", "Testing subtask 2", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(2), Duration.ofMinutes(5));
        int subtask2Id = taskManager.addSubtask(s2);

        Subtask s3 = new Subtask("Test subtask 3", "Testing subtask 3", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(3), Duration.ofMinutes(5));
        int subtask3Id = taskManager.addSubtask(s3);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Неправильный код ответа либо неуспешное получение списка приоритетов");

        List<Task> histTasksList = gson.fromJson(response.body(), new TaskListToken().getType());

        assertEquals(4, histTasksList.size(), "Количество задач в списке приоритетов, полученных через HTTP, отличается от ожидаемого");
    }

}
