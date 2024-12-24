package utils;

import controllers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;

public class TestTaskManager {
    TaskManager taskManager;

    public TestTaskManager() {
        taskManager = new TaskManager();
    }

    public void testTaskManager() {

        System.out.println("-".repeat(46)+"\n\rтрениреуемся с тасками:\n\r1.создать несколько новых");

        int task1Id = taskManager.addTask(new Task("task1", "ПОмыть машЫну",  TaskState.NEW));
        int task2Id = taskManager.addTask(new Task("task2", "постирать чехлы сидений", TaskState.IN_PROGRESS));
        int task3Id = taskManager.addTask(new Task("task3", "переобуть колёса", TaskState.DONE));
        printTasks();

        System.out.println("\n\r2. первую изменим, вторую удалим, еще одну добавим");
        // -- изменение/обновление существующей задачи
        Task task1 = taskManager.getTask(task1Id);
        task1.setDescription("вымыть машину");
        task1.setStatus(TaskState.IN_PROGRESS);
        taskManager.updateTask(task1);
        // -- удаление задачи
        taskManager.deleteTask(task2Id);
        // -- пытаемся обновить отсутствующую задачу - то есть добавляем в список
        Task task4 = new Task("task4", "полировка ЛКП",  TaskState.NEW);
        task4.setId(taskManager.getId());
        taskManager.updateTask(task4);
        // --
        printTasks();

        System.out.println("\n\r3. очистим список задач");
        taskManager.deleteAllTasks();
        printTasks();

        System.out.println("\n\rтренировку с тасками закончили");

        System.out.println("-".repeat(46)+"\n\r\n\rтрениреуемся с эпиками и подзадачами:\n\r1.создать несколько новых");
        int epic1Id = taskManager.addEpic(new Epic("epic1", "купить продуктов"));
        int subtask11Id = taskManager.addSubtask(new Subtask("subtask1_1", "купить хлеба", epic1Id, TaskState.NEW));
        int subtask12Id = taskManager.addSubtask(new Subtask("subtask1_2", "купить молока", epic1Id, TaskState.NEW));
        int subtask13Id = taskManager.addSubtask(new Subtask("subtask1_3", "купить картошки", epic1Id, TaskState.NEW));
        int subtask14Id = taskManager.addSubtask(new Subtask("subtask1_4", "купить чай", epic1Id, TaskState.NEW));

        int epic2Id = taskManager.addEpic(new Epic("epic2", "делать уроки с детьми"));
        int subtask21Id = taskManager.addSubtask(new Subtask("subtask2_1", "делать алгебру", epic2Id, TaskState.NEW));
        int subtask22Id = taskManager.addSubtask(new Subtask("subtask2_2", "делать геометрию", epic2Id, TaskState.NEW));
        int subtask23Id = taskManager.addSubtask(new Subtask("subtask2_3", "делать физику", epic2Id, TaskState.NEW));
        int subtask24Id = taskManager.addSubtask(new Subtask("subtask2_4", "делать информатику", epic2Id, TaskState.NEW));
        int subtask25Id = taskManager.addSubtask(new Subtask("subtask2_5", "делать английский", epic2Id, TaskState.NEW));

        int epic3Id = taskManager.addEpic(new Epic("epic3", "поехать на дачу"));
        int subtask31Id = taskManager.addSubtask(new Subtask("subtask3_1", "тошнить в пробке", epic3Id, TaskState.NEW));
        int subtask32Id = taskManager.addSubtask(new Subtask("subtask3_2", "чистить снег", epic3Id, TaskState.NEW));
        int subtask33Id = taskManager.addSubtask(new Subtask("subtask3_3", "топить печку", epic3Id, TaskState.NEW));

        printAll();

        System.out.println("\n\r2. меняем статус первой подзадаче первого эпика");
        Subtask subtask11 = taskManager.getSubtask(subtask11Id);
        subtask11.setStatus(TaskState.IN_PROGRESS);
        taskManager.updateSubtask(subtask11);
        System.out.println("2.1 смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.2 завершаем все подзадачи первого эпика");
        // --
        subtask11.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask11);
        // --
        Subtask subtask12 = taskManager.getSubtask(subtask12Id);
        subtask12.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask12);
        // --
        Subtask subtask13 = taskManager.getSubtask(subtask13Id);
        subtask13.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask13);
        // --
        Subtask subtask14 = taskManager.getSubtask(subtask14Id);
        subtask14.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask14);
        // --
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.3 удалим три подзадачи первого эпика");
        taskManager.deleteSubtask(subtask11Id);
        taskManager.deleteSubtask(subtask12Id);
        taskManager.deleteSubtask(subtask13Id);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.4 меняем статус оставшейся подзадаче первого эпика");
        subtask14.setStatus(TaskState.IN_PROGRESS);
        taskManager.updateSubtask(subtask14);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.5 удаляем последнюю подзадачу в первом эпике");
        taskManager.deleteSubtask(subtask14Id);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.6 стартуем первую подзадачу второго эпика");
        Subtask subtask21 = taskManager.getSubtask(subtask21Id);
        subtask21.setStatus(TaskState.IN_PROGRESS);
        taskManager.updateSubtask(subtask21);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic2Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.7 удаляем все подзадачи второго эпика");
        Epic epic2 = taskManager.getEpic(epic2Id);
        taskManager.deleteAllSubtasksByEpic(epic2);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic2Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.8 изменяем описание второго эпика");
        epic2.setDescription("доделать сегодня вечером все уроки со всеми детьми");
        taskManager.updateEpic(epic2);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic2Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.9 удаляем второй эпик");
        taskManager.deleteEpic(epic2);
        printEpics(true);

        System.out.println("\n\r2.10 удаляем все подзадачи всех эпиков");
        taskManager.deleteAllSubtasks();
        System.out.println("смотрим что получилось");
        printAll();

        System.out.println("\n\r2.11 удаляем все эпики");
        taskManager.deleteAllEpics();
        printAll();

        System.out.println("-".repeat(46)+"\n\rТестовый прогон программы завершен. Смотрим, что получилось");
        printAll();

        System.out.println("\n\rВсе!\n\r" + "-".repeat(46));

    }

    public void printTasks() {
        if (taskManager.getTaskList().isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        System.out.println("Список задач (всего " + taskManager.getTaskList().size() + " позиций) :");
        for(Task task : taskManager.getTaskList()) {
            System.out.println(task);
        }
    }

    public void printSubtasks() {
        if (taskManager.getSubtaskList().isEmpty()) {
            System.out.println("Список подзадач пуст");
            return;
        }
        System.out.println("Список подзадач (всего " + taskManager.getSubtaskList().size() +
                " позиций; это полный перечень без разбивки по эпикам):");
        for(Subtask subtask : taskManager.getSubtaskList()) {
            System.out.println(subtask);
        }
    }

    public void printEpics(boolean isShortMode) {
        if (taskManager.getEpicList().isEmpty()) {
            System.out.println("Список эпиков пуст");
            return;
        }
        System.out.print("Список эпиков (всего " + taskManager.getEpicList().size() + " позиций) ");
        if (isShortMode) {
            System.out.println("(краткий формат):");
            for(Epic epic : taskManager.getEpicList()) {
                System.out.println(epic);
            }
        } else {
            System.out.println("(полный формат):");
            for(Epic epic : taskManager.getEpicList()) {
                epic.printEpic(taskManager.getSubtaskList());
            }
        }
    }

    public void printAll() {
        System.out.println("Полный перечень всех заданий:");
        printTasks();
        printEpics(true);
        printSubtasks();
    }

}
