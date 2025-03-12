package controllers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {
    int getId();

    ArrayList<Task> getTaskList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Epic> getEpicList();

    // блок работы с задачами (model.Task)
    Task getTask(int taskId);

    int addTask(Task task);

    void updateTask(Task newTask);

    void deleteTask(int taskId);

    void deleteAllTasks();

    // блок работы с Эпиками (model.Epic)
    Epic getEpic(int epicId);

    int addEpic(Epic epic);

    void updateEpic(Epic newEpic);

    void deleteEpic(Epic e);

    void deleteAllEpics();

    // блок работы с Подзадачами (model.Subtask)
    Subtask getSubtask(int subtaskId);

    int addSubtask(Subtask subtask);

    int updateSubtask(Subtask newSubtask);

    void deleteSubtask(int subtaskId);

    void deleteAllSubtasks();

    void deleteAllSubtasksByEpic(Epic e);

    ArrayList<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();
}
