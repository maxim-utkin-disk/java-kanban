package utils;

import controllers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;

import java.util.ArrayList;

public class CheckTaskManager {
    TaskManager taskManager;

    public CheckTaskManager() {
        taskManager = Managers.getDefaultManager();
    }

    public void startChecking() {

        System.out.println("-".repeat(46) + "\n\rтрениреуемся с тасками:\n\r1.создать несколько новых");

        int task1Id = taskManager.addTask(new Task("task1", "ПОмыть машЫну", TaskState.NEW));
        int task2Id = taskManager.addTask(new Task("task2", "постирать чехлы сидений", TaskState.IN_PROGRESS));
        int task3Id = taskManager.addTask(new Task("task3", "переобуть колёса", TaskState.DONE));
        printAll();

        System.out.println("\n\r2. первую изменим, вторую удалим, еще одну добавим");
        // -- изменение/обновление существующей задачи
        Task task1 = taskManager.getTask(task1Id);
        task1.setDescription("вымыть машину");
        task1.setStatus(TaskState.IN_PROGRESS);
        taskManager.updateTask(task1);
        // -- удаление задачи
        taskManager.deleteTask(task2Id);
        // -- пытаемся обновить отсутствующую задачу - то есть добавляем в список
        Task task4 = new Task("task4", "полировка ЛКП", TaskState.NEW);
        task4.setId(taskManager.getId());
        taskManager.updateTask(task4);
        // --
        // -- тренируем историю
        Task task41 = taskManager.getTask(task4.getId());
        Task task11 = taskManager.getTask(task1.getId());
        Task task21 = taskManager.getTask(task2Id);
        Task task31 = taskManager.getTask(task3Id);
        Task task42 = taskManager.getTask(task4.getId());
        Task task12 = taskManager.getTask(task1.getId());
        Task task22 = taskManager.getTask(task2Id);
        Task task32 = taskManager.getTask(task3Id);
        // --
        printAll();

        System.out.println("\n\r3. очистим список задач");
        taskManager.deleteAllTasks();
        printAll();

        System.out.println("\n\rтренировку с тасками закончили");

        System.out.println("-".repeat(46) + "\n\r\n\rтрениреуемся с эпиками и подзадачами:\n\r1.создать несколько новых");
        int epic1Id = taskManager.addEpic(new Epic("epic1", "купить продуктов"));

        Subtask subtask11 = new Subtask("subtask1_1", "купить хлеба", epic1Id, TaskState.NEW);
        int subtask11Id = taskManager.addSubtask(subtask11);
        checkSubtaskOperation(subtask11Id, subtask11);

        Subtask subtask12 = new Subtask("subtask1_2", "купить молока", epic1Id, TaskState.NEW);
        int subtask12Id = taskManager.addSubtask(subtask12);
        checkSubtaskOperation(subtask12Id, subtask12);

        Subtask subtask13 = new Subtask("subtask1_3", "купить картошки", epic1Id, TaskState.NEW);
        int subtask13Id = taskManager.addSubtask(subtask13);
        checkSubtaskOperation(subtask13Id, subtask13);

        Subtask subtask14 = new Subtask("subtask1_4", "купить чай", epic1Id, TaskState.NEW);
        int subtask14Id = taskManager.addSubtask(subtask14);
        checkSubtaskOperation(subtask14Id, subtask14);

        int epic2Id = taskManager.addEpic(new Epic("epic2", "делать уроки с детьми"));

        Subtask subtask21 = new Subtask("subtask2_1", "делать алгебру", epic2Id, TaskState.NEW);
        int subtask21Id = taskManager.addSubtask(subtask21);
        checkSubtaskOperation(subtask21Id, subtask21);

        Subtask subtask22 = new Subtask("subtask2_2", "делать геометрию", epic2Id, TaskState.NEW);
        int subtask22Id = taskManager.addSubtask(subtask22);
        checkSubtaskOperation(subtask22Id, subtask22);

        Subtask subtask23 = new Subtask("subtask2_3", "делать физику", epic2Id, TaskState.NEW);
        int subtask23Id = taskManager.addSubtask(subtask23);
        checkSubtaskOperation(subtask23Id, subtask23);

        Subtask subtask24 = new Subtask("subtask2_4", "делать информатику", epic2Id, TaskState.NEW);
        int subtask24Id = taskManager.addSubtask(subtask24);
        checkSubtaskOperation(subtask24Id, subtask24);

        Subtask subtask25 = new Subtask("subtask2_5", "делать английский", epic2Id, TaskState.NEW);
        int subtask25Id = taskManager.addSubtask(subtask25);
        checkSubtaskOperation(subtask25Id, subtask25);

        int epic3Id = taskManager.addEpic(new Epic("epic3", "поехать на дачу"));

        Subtask subtask31 = new Subtask("subtask3_1", "тошнить в пробке", epic3Id, TaskState.NEW);
        int subtask31Id = taskManager.addSubtask(subtask31);
        checkSubtaskOperation(subtask31Id, subtask31);

        Subtask subtask32 = new Subtask("subtask3_2", "чистить снег", epic3Id, TaskState.NEW);
        int subtask32Id = taskManager.addSubtask(subtask32);
        checkSubtaskOperation(subtask32Id, subtask32);

        Subtask subtask33 = new Subtask("subtask3_3", "топить печку", epic3Id, TaskState.NEW);
        int subtask33Id = taskManager.addSubtask(subtask33);
        checkSubtaskOperation(subtask33Id, subtask33);

        printAll();

        System.out.println("\n\r2. меняем статус первой подзадаче первого эпика");
        //Subtask subtask11 = taskManager.getSubtask(subtask11Id);
        subtask11.setStatus(TaskState.IN_PROGRESS);
        taskManager.updateSubtask(subtask11);
        System.out.println("2.1 смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.2 завершаем все подзадачи первого эпика");
        // --
        subtask11.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask11);
        // --
        //Subtask subtask12 = taskManager.getSubtask(subtask12Id);
        subtask12.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask12);
        // --
        //Subtask subtask13 = taskManager.getSubtask(subtask13Id);
        subtask13.setStatus(TaskState.DONE);
        taskManager.updateSubtask(subtask13);
        // --
        //Subtask subtask14 = taskManager.getSubtask(subtask14Id);
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
        //Subtask subtask21 = taskManager.getSubtask(subtask21Id);
        subtask21.setStatus(TaskState.IN_PROGRESS);
        checkSubtaskOperation(taskManager.updateSubtask(subtask21), subtask21);
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

        System.out.println("-".repeat(46) + "\n\rТестовый прогон программы завершен. Смотрим, что получилось");
        printAll();

        System.out.println("\n\rВсе!\n\r" + "-".repeat(46));

    }

    public void startCheckingDoublyLinkedList() {

        System.out.println("-".repeat(46) + "\n\rтренировка с двусвязным списком");

        int task1Id = taskManager.addTask(new Task("task1", "помыть машину", TaskState.NEW));
        int task2Id = taskManager.addTask(new Task("task2", "постирать чехлы сидений", TaskState.IN_PROGRESS));
        int task3Id = taskManager.addTask(new Task("task3", "переобуть колёса", TaskState.DONE));
        int task4Id = taskManager.addTask(new Task("task4", "купить в леруа новые водосчетчики", TaskState.NEW));
        int task5Id = taskManager.addTask(new Task("task5", "вызвать слесаря для замены водосчетчиков", TaskState.IN_PROGRESS));
        int task6Id = taskManager.addTask(new Task("task6", "отпроситься на работе для замены водосчетиков", TaskState.DONE));
        int task7Id = taskManager.addTask(new Task("task7", "купить в леруа столярный клей", TaskState.NEW));
        int task8Id = taskManager.addTask(new Task("task8", "найти наждачку", TaskState.NEW));
        int task9Id = taskManager.addTask(new Task("task9", "разобрать расшатанные стулья", TaskState.NEW));
        int task10Id = taskManager.addTask(new Task("task10", "зачистить расшатанные стулья", TaskState.NEW));
        int task11Id = taskManager.addTask(new Task("task11", "склеить расшатанные стулья", TaskState.NEW));
        int task12Id = taskManager.addTask(new Task("task12", "собрать склеенные стулья", TaskState.NEW));

        Task task121 = taskManager.getTask(task12Id);
        Task task111 = taskManager.getTask(task11Id);
        Task task101 = taskManager.getTask(task10Id);
        Task task91 = taskManager.getTask(task9Id);
        Task task81 = taskManager.getTask(task8Id);
        Task task71 = taskManager.getTask(task7Id);
        Task task61 = taskManager.getTask(task6Id);
        Task task51 = taskManager.getTask(task5Id);
        Task task41 = taskManager.getTask(task4Id);
        Task task31 = taskManager.getTask(task3Id);
        Task task21 = taskManager.getTask(task2Id);
        Task task11 = taskManager.getTask(task1Id);

        Task task122 = taskManager.getTask(task12Id);
        Task task112 = taskManager.getTask(task11Id);
        Task task102 = taskManager.getTask(task10Id);
        Task task92 = taskManager.getTask(task9Id);
        Task task82 = taskManager.getTask(task8Id);
        Task task72 = taskManager.getTask(task7Id);
        Task task62 = taskManager.getTask(task6Id);
        Task task52 = taskManager.getTask(task5Id);
        Task task42 = taskManager.getTask(task4Id);
        Task task32 = taskManager.getTask(task3Id);
        Task task22 = taskManager.getTask(task2Id);
        Task task12 = taskManager.getTask(task1Id);

        Task task13 = taskManager.getTask(task1Id);
        Task task23 = taskManager.getTask(task2Id);
        Task task33 = taskManager.getTask(task3Id);
        Task task43 = taskManager.getTask(task4Id);
        Task task53 = taskManager.getTask(task5Id);
        Task task63 = taskManager.getTask(task6Id);
        Task task73 = taskManager.getTask(task7Id);
        Task task83 = taskManager.getTask(task8Id);
        Task task93 = taskManager.getTask(task9Id);
        Task task103 = taskManager.getTask(task10Id);
        Task task113 = taskManager.getTask(task11Id);
        Task task123 = taskManager.getTask(task12Id);

        System.out.print("\n\r - история после добавления 12-ти задач и запроса их несколько раз:");
        printHistory();

        for (Task t : taskManager.getTaskList()) {
           if (t.getId() % 2 == 0) {
               taskManager.deleteTask(t.getId());
           }
        }
        Task task124 = taskManager.getTask(task12Id);
        Task task24 = taskManager.getTask(task2Id);
        Task task104 = taskManager.getTask(task10Id);
        Task task44 = taskManager.getTask(task4Id);
        Task task84 = taskManager.getTask(task8Id);
        Task task64 = taskManager.getTask(task6Id);

        System.out.print("\n\r - история после удаления задач с четными id и запроса оставшихся в произвольном порядке:");
        printHistory();

        int epic1Id = taskManager.addEpic(new Epic("epic1", "Самоподготовка"));
        int subtask1Id = taskManager.addSubtask(new Subtask("subtask1", "освоить Java", epic1Id, TaskState.IN_PROGRESS));
        int subtask2Id = taskManager.addSubtask(new Subtask("subtask2", "переучиться с Oracle на Postgres", epic1Id, TaskState.IN_PROGRESS));
        int subtask3Id = taskManager.addSubtask(new Subtask("subtask3", "вспомнить основы работы в *NIX", epic1Id, TaskState.NEW));
        int subtask4Id = taskManager.addSubtask(new Subtask("subtask3", "разобраться с ЭП и сертификатами", epic1Id, TaskState.NEW));

        int epic2Id = taskManager.addEpic(new Epic("epic2", "начать тихо искать новую работу, чтобы нынешние не заметили :-)"));

        Epic e2 = taskManager.getEpic(epic2Id);
        Subtask s4 = taskManager.getSubtask(subtask4Id);
        Subtask s1 = taskManager.getSubtask(subtask1Id);
        Epic e1 = taskManager.getEpic(epic1Id);
        Subtask s2 = taskManager.getSubtask(subtask2Id);
        Subtask s3 = taskManager.getSubtask(subtask3Id);

        System.out.print("\n\r - история после добавления эпиков и подзадач в произвольном порядке:");
        printHistory();

        taskManager.deleteEpic(e1);
        System.out.print("\n\r - история после удаления эпика с подзадачами:");
        printHistory();

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
            for (Epic epic : taskManager.getEpicList()) {
                System.out.println(epic);
            }
        } else {
            System.out.println("(полный формат):");
            for (Epic epic : taskManager.getEpicList()) {
                epic.printEpic(taskManager.getSubtaskList());
            }
        }
    }

    public void printAll() {
        System.out.println("Полный перечень всех заданий:");
        printTasks();
        printEpics(true);
        printSubtasks();
        printHistory();
    }

    public void printHistory() {
        ArrayList<Task> taskHistory = taskManager.getHistory();
        System.out.println("\n\r" + "-".repeat(10) +  " >>> выводим историю просмотров задач:");
        int i = 0;
        for (Task task : taskHistory) {
            System.out.println(++i + ". " + task);
        }
        System.out.println("-".repeat(10) + "<<< закончили вывод истории просмотров задач");
    }

    public void checkSubtaskOperation(int resultCode, Subtask s) {
        // положительные значения resultCode - валидные id, их не анализируем
        if (resultCode == -1) {
            System.out.println("Добавление/обновление подзадачи subtaskId=" + s.getId() +
                    ", name=" + s.getName() + ", description=" + s.getDescription() +
                    " не выполнено, те не найден эпик epicId=" + s.getEpicId());
        } else if (resultCode < -1) {
            System.out.println("Произошла неустановленная ошибка при обработке подзадачи subtaskId=" + s.getId() +
                    ", name=" + s.getName() + ", description=" + s.getDescription() + ", epicId=" + s.getEpicId());
        }
    }
}
