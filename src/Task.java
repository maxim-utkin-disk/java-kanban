import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int id;
    protected TaskState status;

    public Task(String name, String description, int id, TaskState status) {
        this.name = name;
        this.description = description;
        this.id = id;
        if (status == null) {
            this.status = TaskState.NEW;
        } else {
            this.status = status;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskState getStatus() {
        return status;
    }

    // в ТЗ просят сравнивать только id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    // в ТЗ просят сравнивать только id
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Задача (task) id=" + id +
                ", Наименование = " + name +
                ", Описание = " + description +
                ", Статус = " + status.toString();
    }
}
