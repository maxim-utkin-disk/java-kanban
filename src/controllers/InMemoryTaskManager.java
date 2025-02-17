package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HashMap<Integer, Epic> epicList;

    private static int id;

    protected final HistoryManager historyMgr;


    public InMemoryTaskManager() {
        id = -1;
        taskList = new HashMap<>();
        subtaskList = new HashMap<>();
        epicList = new HashMap<>();
        historyMgr = Managers.getDefaultHistory();
    }

    @Override
    public int getId() {
        // оставлюю на тот случай, если для генерации id потом будет свой алгоритм
        return ++id;
    }

    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicList.values());
    }

    // блок работы с задачами (model.Task)
    @Override
    public Task getTask(int taskId) {
        Task t = taskList.get(taskId);
        historyMgr.add(t);
        return t;
    }

    @Override
    public int addTask(Task task) {
        if (task.getId() == -1) {
            task.setId(this.getId());
        }
        taskList.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task newTask) {
        if (!taskList.containsKey(newTask.getId())) {
            addTask(newTask);
        } else {
            taskList.replace(newTask.getId(), newTask);
        }
    }

    @Override
    public void deleteTask(int taskId) {
        historyMgr.remove(taskId);
        taskList.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : taskList.keySet()) {
           historyMgr.remove(taskId);
        }
        taskList.clear();
    }

    // блок работы с Эпиками (model.Epic)
    @Override
    public Epic getEpic(int epicId) {
        Epic e = epicList.get(epicId);
        historyMgr.add(e);
        return e;
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic.getId() == -1) {
            epic.setId(this.getId());
        }
        epicList.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (!epicList.containsKey(newEpic.getId())) {
            addEpic(newEpic);
        } else {
            epicList.replace(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void deleteEpic(Epic e) {
        for (int subtaskId : e.getSubtaskIdsList()) {
            historyMgr.remove(subtaskId);
        }
        historyMgr.remove(e.getId());
        deleteAllSubtasksByEpic(e);
        epicList.remove(e.getId());
    }

    @Override
    public void deleteAllEpics() {
        for (Integer subtaskId : subtaskList.keySet()) {
            historyMgr.remove(subtaskId);
        }
        for (Integer epicId : epicList.keySet()) {
            historyMgr.remove(epicId);
        }
        subtaskList.clear();
        epicList.clear();
    }

    // блок работы с Подзадачами (model.Subtask)
    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask s = subtaskList.get(subtaskId);
        historyMgr.add(s);
        return s;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (!epicList.containsKey(subtask.getEpicId())) {
            return -1;
        } else {
            if (subtask.getId() == -1) {
                subtask.setId(this.getId());
            }
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            epicList.get(subtask.getEpicId()).actualizeStatus(getSubtaskList());
            return subtask.getId();
        }
    }

    @Override
    public int updateSubtask(Subtask newSubtask) {
        if (!epicList.containsKey(newSubtask.getEpicId())) {
            return -1;
        }

        if (!subtaskList.containsKey(newSubtask.getId())) {
            addSubtask(newSubtask);
        } else {
            subtaskList.replace(newSubtask.getId(), newSubtask);
        }

        Epic e = epicList.get(newSubtask.getEpicId());
        e.actualizeStatus(getSubtaskList());

        return 0;
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        if (!subtaskList.containsKey(subtaskId)) {
            return;
        }
        Epic epic = getEpic(getSubtask(subtaskId).getEpicId());
        epic.deleteSubtaskId(subtaskId);
        subtaskList.remove(subtaskId);
        epic.actualizeStatus(getSubtaskList());
        historyMgr.remove(subtaskId);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer subtaskId : subtaskList.keySet()) {
            historyMgr.remove(subtaskId);
        }
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.getSubtaskIdsList().clear();
            epic.actualizeStatus(getSubtaskList());
        }
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic e) {
        for (int i = getSubtaskList().size() - 1; i >= 0; i--) {
            Subtask s = getSubtaskList().get(i);
            if (s.getEpicId() == e.getId()) {
                historyMgr.remove(s.getId());
                subtaskList.remove(s.getId());
            }
        }
        e.getSubtaskIdsList().clear();
        e.actualizeStatus(getSubtaskList());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyMgr.getHistory();
    }

    protected void setActualId(int actualId) {
        id = actualId;
    }

    protected TaskType getTaskTypeById (int id) {
        if (taskList.containsKey(id)) {
            return TaskType.TASK;
        } else if (epicList.containsKey(id)) {
            return TaskType.EPIC;
        } else if (subtaskList.containsKey(id)) {
            return TaskType.SUBTASK;
        } else {
            return null;
        }
    }

}
