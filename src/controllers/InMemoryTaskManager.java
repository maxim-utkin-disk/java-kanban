package controllers;

import exceptions.TaskTimeExecIntersectTime;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import utils.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HashMap<Integer, Epic> epicList;

    private static int id;

    protected final HistoryManager historyMgr;

    protected TreeSet<Task> prioritizedTasksList;


    public InMemoryTaskManager() {
        id = -1;
        taskList = new HashMap<>();
        subtaskList = new HashMap<>();
        epicList = new HashMap<>();
        historyMgr = Managers.getDefaultHistory();
        prioritizedTasksList = new TreeSet<>();
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
        if (this.hasIntersectWithOtherTask(task)) {
            throw new TaskTimeExecIntersectTime("Добавляемая задача name = \"" + task.getName() +
                    "\" пересекается по времени с другими задачами. В список не добавлена.");
        }

        if (task.getId() == -1) {
            task.setId(this.getId());
        }
        taskList.put(task.getId(), task);
        addToPrioritizedTasksList(task);
        return task.getId();
    }

    @Override
    public void updateTask(Task newTask) {
        if (this.hasIntersectWithOtherTask(newTask)) {
            throw new TaskTimeExecIntersectTime("Обновляемая задача name = \"" + newTask.getName() +
                    "\" пересекается по времени с другими задачами. В список не добавлена.");
        }

        if (!taskList.containsKey(newTask.getId())) {
            addTask(newTask);
        } else {
            taskList.replace(newTask.getId(), newTask);
        }
        addToPrioritizedTasksList(newTask);
    }

    @Override
    public void deleteTask(int taskId) {
        prioritizedTasksList.remove(taskList.get(taskId));
        historyMgr.remove(taskId);
        taskList.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        taskList.keySet().forEach(taskId -> {
            historyMgr.remove(taskId);
            prioritizedTasksList.remove(taskList.get(taskId));
        });
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
        deleteAllSubtasksByEpic(e);
        historyMgr.remove(e.getId());
        deleteAllSubtasksByEpic(e);
        epicList.remove(e.getId());
    }

    @Override
    public void deleteAllEpics() {
        subtaskList.keySet().forEach(subtaskId -> {
            historyMgr.remove(subtaskId);
            prioritizedTasksList.remove(subtaskList.get(subtaskId));
        });

        epicList.keySet().forEach(historyMgr::remove);
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
            if (this.hasIntersectWithOtherTask(subtask)) {
                throw new TaskTimeExecIntersectTime("Добавляемая подзадача name = \"" + subtask.getName() +
                        "\" пересекается по времени с другими задачами. В список не добавлена.");
            }
            if (subtask.getId() == -1) {
                subtask.setId(this.getId());
            }
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpicId()).linkSubtaskToEpic(subtask);
            addToPrioritizedTasksList(subtask);
            return subtask.getId();
        }
    }

    @Override
    public int updateSubtask(Subtask newSubtask) {
        if (!epicList.containsKey(newSubtask.getEpicId())) {
            return -1;
        }

        if (this.hasIntersectWithOtherTask(newSubtask)) {
            throw new TaskTimeExecIntersectTime("Обновляемая подзадача name = \"" + newSubtask.getName() +
                    "\" пересекается по времени с другими задачами. В список не добавлена.");
        }

        if (!subtaskList.containsKey(newSubtask.getId())) {
            addSubtask(newSubtask);
        } else {
            subtaskList.replace(newSubtask.getId(), newSubtask);
            Epic e = epicList.get(newSubtask.getEpicId());
            e.actualizeStatus();
            e.actualizeEpicExecutionPeriod();
        }

        addToPrioritizedTasksList(newSubtask);

        return 0;
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        if (!subtaskList.containsKey(subtaskId)) {
            return;
        }
        Subtask s = subtaskList.get(subtaskId);
        prioritizedTasksList.remove(s);
        Epic epic = epicList.get(s.getEpicId());
        epic.unlinkSubtaskFromEpic(s);
        subtaskList.remove(subtaskId, s);
        historyMgr.remove(subtaskId);
    }

    @Override
    public void deleteAllSubtasks()    {
        subtaskList.keySet().forEach(subtaskId -> {
          historyMgr.remove(subtaskId);
          prioritizedTasksList.remove(subtaskList.get(subtaskId));
          });

        subtaskList.clear();
        epicList.values()
                .stream()
                .forEach(epic -> epic.unlinkAllSubtasksFromEpic());
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic e) {
        e.getSubtasksPerEpic().stream()
                        .forEach(subtask -> {
                            subtaskList.remove(subtask.getId(), subtask);
                            historyMgr.remove(subtask.getId());
                            prioritizedTasksList.remove(subtask);
                        });
        e.unlinkAllSubtasksFromEpic();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyMgr.getHistory();
    }

    protected void setActualId(int actualId) {
        id = actualId;
    }

    protected Optional<TaskType> getTaskTypeById(int id) {
        if (taskList.containsKey(id)) {
            return Optional.of(TaskType.TASK);
        } else if (epicList.containsKey(id)) {
            return Optional.of(TaskType.EPIC);
        } else if (subtaskList.containsKey(id)) {
            return Optional.of(TaskType.SUBTASK);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasksList);
    }

    public void addToPrioritizedTasksList(Task t) {
        if (prioritizedTasksList.contains(t)) {
            prioritizedTasksList.remove(t);
        }
        if (t.getStartTime() != null) {
            if (!hasIntersectWithOtherTask(t)) {
                prioritizedTasksList.add(t);
            }
        }
    }

    public boolean hasIntersectWithOtherTask(Task t) {
        if (t.getStartTime() == null) {
            return false;
        }

        return prioritizedTasksList.stream()
                .filter(taskFromList -> taskFromList.getId() != t.getId())
                .filter(taskFromList -> taskFromList.getStartTime().isBefore(t.getEndTime()) &&
                                        taskFromList.getEndTime().isAfter(t.getStartTime()))
                .count() > 0;
    }



}
