public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int id, int epicId, TaskState status) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Подзадача (subtask) id=" + getId() +
                ", Наименование = " + getName() +
                ", Описание = " + getDescription() +
                ", Статус = " + getStatus().toString() +
                ", epicId = " + epicId;
    }
}