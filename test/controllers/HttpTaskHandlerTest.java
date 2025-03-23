package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import http.HttpTaskServer;
import model.Task;
import model.TaskState;
import org.junit.jupiter.api.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskHandlerTest {

    static TaskManager taskManager = Managers.getDefaultManager();
    static HttpTaskServer hts;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskHandlerTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Testing task",
                TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправильный код ответа либо неуспешное добавление");

        assertNotNull(taskManager.getTaskList(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getTaskList().size(), "Некорректное количество задач");
        assertEquals("Test task", taskManager.getTaskList().get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Testing task",
                TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson2Add = gson.toJson(task);

        HttpClient client2Add = HttpClient.newHttpClient();
        URI url2Add = URI.create("http://localhost:8080/tasks");
        HttpRequest request2Add = HttpRequest.newBuilder()
                .uri(url2Add)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2Add))
                .build();
        HttpResponse<String> response2Add = client2Add.send(request2Add, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2Add.statusCode(), "Неправильный код ответа либо неудалчное добавление задачи");

        ArrayList<Task> tasksList = taskManager.getTaskList();
        task.setId(tasksList.get(0).getId());
        task.setName(task.getName() + " updated");
        task.setDescription(task.getDescription() + " updated");
        String taskJson2Upd = gson.toJson(task);

        HttpClient client2Upd = HttpClient.newHttpClient();
        URI url2Upd = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request2Upd = HttpRequest.newBuilder()
                .uri(url2Upd)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2Upd))
                .build();
        HttpResponse<String> response2Upd = client2Upd.send(request2Upd, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2Upd.statusCode(), "Неправильный код ответа либо неудачное обновление задачи");

        tasksList = taskManager.getTaskList();

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(1, tasksList.size(), "Некорректное количество задач после обновления");
        assertEquals("Test task updated", tasksList.get(0).getName(), "Некорректное имя задачи после обновления");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Testing task",
                TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо неуспешное удаление");
        assertEquals(0, taskManager.getTaskList().size(), "Некорректное количество задач после удаления");
    }

    @Test
    public void testGetTaskList() throws IOException, InterruptedException {
        Task task1 = new Task("Test task 1", "Testing task 1",
                TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task1);

        Task task2 = new Task("Test task 2", "Testing task 2",
                TaskState.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо ошибка получения списка задач");

        List<Task> tasksList = gson.fromJson(response.body(), new TaskListToken().getType());

        assertEquals(2, tasksList.size(), "Количество задач, полученных через HTTP, отличается от ожидаемого");
        assertEquals(task1.getName(), tasksList.get(0).getName(), "Наименование задачи 1 из JSON отличается от наименования из кода");
        assertEquals(task2.getName(), tasksList.get(1).getName(), "Наименование задачи 2 из JSON отличается от наименования из кода");
    }

    @Test
    public void testGetOneTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test task 1", "Testing task 1",
                TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо ошибка получения одной задачи");

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        assertEquals(task1.getName(), jsonObject.get("name").getAsString(),
                "Наименование задачи 1 из JSON отличается от наименования из кода");
        assertEquals(task1.getDescription(), jsonObject.get("description").getAsString()
                , "Примечание к задаче 1 из JSON отличается от примечания из кода");
    }

}
