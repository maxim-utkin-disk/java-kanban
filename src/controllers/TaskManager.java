package controllers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HashMap<Integer, Epic> epicList;

    private static int id;

    public TaskManager() {
        id = -1;
        taskList = new HashMap<>();
        subtaskList = new HashMap<>();
        epicList = new HashMap<>();
    }

    public int getId() {
        // оставлюю на тот случай, если для генерации id потом будет свой алгоритм
        return ++id;
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskList.values());
    }
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicList.values());
    }

    // блок работы с задачами (model.Task)
    public Task getTask(int taskId) {
        return taskList.get(taskId);
    }

    public int addTask(Task task) {
        task.setId(getId());
        taskList.put(task.getId(), task);
        return task.getId();
    }

    public void updateTask(Task newTask) {
        if (!taskList.containsKey(newTask.getId())) {
            addTask(newTask);
        } else {
            taskList.replace(newTask.getId(), newTask);
        }
    }

    public void deleteTask(int taskId) {
        taskList.remove(taskId);
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    // блок работы с Эпиками (model.Epic)
    public Epic getEpic(int epicId) {
        return epicList.get(epicId);
    }

    public int addEpic(Epic epic) {
        epic.setId(getId());
        epicList.put(epic.getId(), epic);
        return epic.getId();
    }

    public void updateEpic(Epic newEpic) {
        if (!epicList.containsKey(newEpic.getId())) {
            addEpic(newEpic);
        } else {
            epicList.replace(newEpic.getId(), newEpic);
        }
    }

    public void deleteEpic(Epic e) {
        deleteAllSubtasksByEpic(e);
        epicList.remove(e.getId());
    }

    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    // блок работы с Подзадачами (model.Subtask)
    public Subtask getSubtask(int subtaskId) {
        return subtaskList.get(subtaskId);
    }

    public int addSubtask(Subtask subtask) {
        if (!epicList.containsKey(subtask.getEpicId())) {
            System.out.println("В списке эпиков id = " + subtask.getEpicId() + "не найден. Подзадача не добавлена!");
            return -1;
        } else {
            subtask.setId(getId());
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            epicList.get(subtask.getEpicId()).actualizeStatus(getSubtaskList());
            return subtask.getId();
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        if (!epicList.containsKey(newSubtask.getEpicId())) {
            System.out.println("Эпик id=" + newSubtask.getEpicId() +
                    " не найден в списке эпиков. Обновление подзадачи не выполнено");
            return;
        }

        if (!subtaskList.containsKey(newSubtask.getId())) {
            addSubtask(newSubtask);
        } else {
            subtaskList.replace(newSubtask.getId(), newSubtask);
        }

        Epic e = epicList.get(newSubtask.getEpicId());
        e.actualizeStatus(getSubtaskList());

    }

    public void deleteSubtask(int subtaskId) {
        if (!subtaskList.containsKey(subtaskId)) {
            System.out.println("Подзадачи с id = " + subtaskId + " нет в списке, удаление не выполнено");
            return;
        }
        Epic epic = getEpic(getSubtask(subtaskId).getEpicId());
        epic.deleteSubtaskId(subtaskId);
        subtaskList.remove(subtaskId);
        epic.actualizeStatus(getSubtaskList());
    }

    public void deleteAllSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.getSubtaskIdsList().clear();
            epic.actualizeStatus(getSubtaskList());
        }
    }

    public void deleteAllSubtasksByEpic(Epic e) {
        for (int i = getSubtaskList().size()-1; i >= 0 ; i--) {
            Subtask s = getSubtaskList().get(i);
            if (s.getEpicId() == e.getId()) {
                subtaskList.remove(s.getId());
            }
        }
        e.getSubtaskIdsList().clear();
        e.actualizeStatus(getSubtaskList());
    }

}
