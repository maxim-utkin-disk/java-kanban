package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.TaskState;
import org.junit.jupiter.api.*;
import utils.*;

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


public class HttpSubtaskHandlerTest {

    static TaskManager taskManager = Managers.getDefaultManager();
    static HttpTaskServer hts;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeAll
    public static void startHttpServer() {
        try {
            hts = new HttpTaskServer(taskManager);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        hts.HttpTaskServerStart();
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

    @AfterAll
    public static void shutDown() {
        hts.HttpTaskServerStop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Testing subtask", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправильный код ответа либо неуспешное добавление подзадачи");

        assertNotNull(taskManager.getSubtaskList(), "Подзадачи не возвращаются");
        assertEquals(1, taskManager.getSubtaskList().size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", taskManager.getSubtaskList().get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Testing subtask", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);

        HttpClient client2Add = HttpClient.newHttpClient();
        URI url2Add = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2Add = HttpRequest.newBuilder()
                .uri(url2Add)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response2Add = client2Add.send(request2Add, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2Add.statusCode(),
                "Неправильный код ответа либо неуспешное добавление подзадачи");

        ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
        subtask.setId(subtaskList.get(0).getId());
        subtask.setName(subtask.getName() + " updated");
        subtask.setDescription(subtask.getDescription() + " updated");
        String subtaskJson2Upd = gson.toJson(subtask);

        HttpClient client2Upd = HttpClient.newHttpClient();
        URI url2Upd = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request2Upd = HttpRequest.newBuilder()
                .uri(url2Upd)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2Upd))
                .build();
        HttpResponse<String> response2Upd = client2Upd.send(request2Upd, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2Upd.statusCode(), "Неправильный код ответа либо неудачное обновление подзадачи");

        subtaskList = taskManager.getSubtaskList();

        assertNotNull(subtaskList, "Подзадачи не возвращаются");
        assertEquals(1, subtaskList.size(), "Некорректное количество подзадач после обновления");
        assertEquals("Test subtask updated", subtaskList.get(0).getName(), "Некорректное имя подзадачи после обновления");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Testing subtask", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5));
        int subtaskId = taskManager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо неуспешное удаление подзадачи");
        assertEquals(0, taskManager.getSubtaskList().size(), "Некорректное количество подзадач после удаления");
    }

    @Test
    public void testGetSubtaskList() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Testing subtask 1", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        int subtask1Id = taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Testing subtask 2", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(2), Duration.ofMinutes(5));
        int subtask2Id = taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Test subtask 3", "Testing subtask 3", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(3), Duration.ofMinutes(5));
        int subtask3d = taskManager.addSubtask(subtask3);


        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Неправильный код ответа либо ошибка получения списка подзадач");

        List<Subtask> subtasksFromHttpList = gson.fromJson(response.body(), new SubtaskListToken().getType());

        assertEquals(3, subtasksFromHttpList.size(),
                "Количество подзадач, полученных через HTTP, отличается от ожидаемого");
        assertEquals(subtask1.getName(), subtasksFromHttpList.get(0).getName(),
                "Наименование подзадачи 1 из JSON отличается от наименования из кода");
        assertEquals(subtask2.getDescription(), subtasksFromHttpList.get(1).getDescription(),
                "Примечание подзадачи 2 из JSON отличается от примечания из кода");
        assertEquals(subtask3.getStatus(), subtasksFromHttpList.get(2).getStatus(),
                "Статус подзадачи 2 из JSON отличается от статус из кода");
    }

    @Test
    public void testGetOneSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Testing subtask 1", epicId,
                TaskState.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        int subtask1Id = taskManager.addSubtask(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1Id);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо ошибка получения одной подзадачи");

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        assertEquals(subtask1.getName(), jsonObject.get("name").getAsString(),
                "Наименование подзадачи из JSON отличается от наименования из кода");
        assertEquals(subtask1.getDescription(), jsonObject.get("description").getAsString()
                , "Примечание к подзадаче из JSON отличается от примечания из кода");
    }

}
