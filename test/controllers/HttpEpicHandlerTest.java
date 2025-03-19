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

public class HttpEpicHandlerTest {

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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic 2");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправильный код ответа либо неуспешное добавление эпика");

        assertNotNull(taskManager.getEpicList(), "Эпики не возвращаются");
        assertEquals(1, taskManager.getEpicList().size(), "Некорректное количество эпиков");
        assertEquals("Test epic", taskManager.getEpicList().get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        String taskJson2Add = gson.toJson(epic);

        HttpClient client2Add = HttpClient.newHttpClient();
        URI url2Add = URI.create("http://localhost:8080/epics");
        HttpRequest request2Add = HttpRequest.newBuilder()
                .uri(url2Add)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2Add))
                .build();
        HttpResponse<String> response2Add = client2Add.send(request2Add, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2Add.statusCode(), "Неправильный код ответа либо неудаченое добавление эпика");

        ArrayList<Epic> epicsList = taskManager.getEpicList();
        epic.setId(epicsList.get(0).getId());
        epic.setName(epic.getName() + " updated");
        epic.setDescription(epic.getDescription() + " updated");
        String epicJson2Upd = gson.toJson(epic);

        HttpClient client2Upd = HttpClient.newHttpClient();
        URI url2Upd = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request2Upd = HttpRequest.newBuilder()
                .uri(url2Upd)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2Upd))
                .build();
        HttpResponse<String> response2Upd = client2Upd.send(request2Upd, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2Upd.statusCode(), "Неправильный код ответа либо неудачное обновление эпика");

        epicsList = taskManager.getEpicList();

        assertNotNull(epicsList, "Эпики не возвращаются");
        assertEquals(1, epicsList.size(), "Некорректное количество эпиков после обновления");
        assertEquals("Test epic updated", epicsList.get(0).getName(), "Некорректное имя эпика после обновления");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо неуспешное удаление эпика");
        assertEquals(0, taskManager.getEpicList().size(), "Некорректное количество эпиков после удаления");
    }

    @Test
    public void testGetEpicList() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test epic 1", "Testing epic 1");
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Test epic 2", "Testing epic 2");
        taskManager.addEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо ошибка получения списка эпиков");

        List<Epic> epicsFromHttpList = gson.fromJson(response.body(), new EpicListToken().getType());

        assertEquals(2, epicsFromHttpList.size(), "Количество эпиков, полученных через HTTP, отличается от ожидаемого");
        assertEquals(epic1.getName(), epicsFromHttpList.get(0).getName(), "Наименование эпика 1 из JSON отличается от наименования из кода");
        assertEquals(epic2.getName(), epicsFromHttpList.get(1).getName(), "Наименование эпика 2 из JSON отличается от наименования из кода");
    }

    @Test
    public void testGetOneEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic 1", "Testing epic 1");
        taskManager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неправильный код ответа либо ошибка получения одного эпика");

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        assertEquals(epic.getName(), jsonObject.get("name").getAsString(),
                "Наименование эпика из JSON отличается от наименования из кода");
        assertEquals(epic.getDescription(), jsonObject.get("description").getAsString()
                , "Примечание к эпику из JSON отличается от примечания из кода");
    }

    @Test
    public void testGetSubtaskListFromEpic() throws IOException, InterruptedException {
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

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Неправильный код ответа либо ошибка получения списка подзадач эпика");

        List<Subtask> subtasksList = gson.fromJson(response.body(), new SubtaskListToken().getType());

        assertEquals(3, subtasksList.size(),
                "Количество подзадач, полученных через HTTP, отличается от ожидаемого");
        assertEquals(s1.getName(), subtasksList.get(0).getName(),
                "Наименование подзадачи 1 эпика из JSON отличается от наименования из кода");
        assertEquals(s2.getDescription(), subtasksList.get(1).getDescription(),
                "Примечание подзадачи 2 эпика из JSON отличается от примечания из кода");
        assertEquals(s3.getStatus(), subtasksList.get(2).getStatus(),
                "Статус подзадачи 3 эпика из JSON отличается от статуса из кода");
    }

}
