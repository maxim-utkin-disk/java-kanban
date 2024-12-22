import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdsList;

    public Epic(String name, String description, int id, TaskState status){
        super(name, description, id, TaskState.NEW);
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

    public void clearAllSubtasks(HashMap<Integer, Subtask> subtaskList) {
        for (int i = 0; i < subtaskIdsList.size(); i++) {
            subtaskList.remove(subtaskIdsList.get(i));
        }
        subtaskIdsList.clear();
        this.status = TaskState.NEW;
    }

    public void printEpic(HashMap<Integer, Subtask> subtaskList) {
        System.out.println(this);
        System.out.println("Подзадачи эпика (всего " + subtaskIdsList.size() + " позиций):");
        for (int i = 0; i < subtaskIdsList.size(); i++) {
            System.out.println(subtaskList.get(subtaskIdsList.get(i)));
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

    public void actualizeStatus(HashMap<Integer, Subtask> subtaskList) {
        if (subtaskIdsList.isEmpty()) {
            this.status = TaskState.NEW;
        } else {
            int cntStatusNew = 0;
            int cntStatusDone = 0;
            for (int i = 0; i < subtaskIdsList.size(); i++) {
                if (subtaskList.get(subtaskIdsList.get(i)).getStatus() == TaskState.NEW) {cntStatusNew++;}
                if (subtaskList.get(subtaskIdsList.get(i)).getStatus() == TaskState.DONE) {cntStatusDone++;}
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
