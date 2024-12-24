package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdsList;

    public Epic(String name, String description){
        super(name, description, TaskState.NEW);
        subtaskIdsList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdsList() {
        return subtaskIdsList;
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIdsList.contains(subtaskId)) {
            subtaskIdsList.add(subtaskId);
        }
    }

    public void deleteSubtaskId(int subtaskId) {
        if (!subtaskIdsList.contains(subtaskId)) {
            System.out.println("позадача с id = " + subtaskId + " отсутствует в списке подазач эпика id = " +
                    getId() + ". Удаление подзадачи невозможно.");
        }
        for (int i = subtaskIdsList.size() - 1; i >= 0; i--) {
            if (subtaskIdsList.get(i) == subtaskId) {
                subtaskIdsList.remove(i);
            }
        }
    }

    public void printEpic(ArrayList<Subtask> subtaskList) {
        System.out.println(this);
        System.out.println("Подзадачи эпика (всего " + subtaskIdsList.size() + " позиций):");
        for (Subtask s : subtaskList) {
            if (s.getEpicId() == this.getId())  {
                System.out.println(s);
            }
        }
    }

    @Override
    public String toString() {
        return "Эпик (epic) id=" + getId() +
                ", Наименование = " + getName() +
                ", Описание = " + getDescription() +
                ", Статус = " + getStatus().toString() +
                ", кол-во подзадач = " + subtaskIdsList.size();
    }

    public void actualizeStatus(ArrayList<Subtask> subtaskList) {
        if (subtaskIdsList.isEmpty()) {
            this.status = TaskState.NEW;
        } else {
            int cntStatusNew = 0;
            int cntStatusDone = 0;
            for (int i = 0; i < subtaskList.size(); i++) {
                Subtask s = subtaskList.get(i);
                if (s.getEpicId() == this.getId()) {
                    if (s.getStatus() == TaskState.NEW) {
                        cntStatusNew++;
                    }
                    if (s.getStatus() == TaskState.DONE) {
                        cntStatusDone++;
                    }
                }
            }
            if (cntStatusNew == subtaskIdsList.size()) {
                this.status = TaskState.NEW;
            } else if (cntStatusDone == subtaskIdsList.size()) {
                this.status = TaskState.DONE;
            } else {
                this.status = TaskState.IN_PROGRESS;
            }
        }
    }

}
