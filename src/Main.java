public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();


        System.out.println("-".repeat(46)+"\n\rтрениреуемся с тасками:\n\r1.создать несколько новых");
        int task1Id = taskManager.getId();
        taskManager.addTask(new Task("task1", "ПОмыть машЫну", task1Id, TaskState.NEW));
        int task2Id = taskManager.getId();
        taskManager.addTask(new Task("task2", "постирать чехлы сидений", task2Id, TaskState.IN_PROGRESS));
        int task3Id = taskManager.getId();
        taskManager.addTask(new Task("task3", "переобуть колёса", task3Id, TaskState.DONE));
        taskManager.printTasks();

        System.out.println("\n\r2. первую изменим, вторую удалим, еще одну добавим");
        taskManager.updateTask(new Task("task4", "полировка ЛКП", taskManager.getId(), TaskState.NEW));
        taskManager.updateTask(new Task("task1", "вымыть машину", task1Id, TaskState.IN_PROGRESS));
        taskManager.deleteTask(task2Id);
        taskManager.printTasks();

        System.out.println("\n\r3. очистим список задач");
        taskManager.deleteAllTasks();
        taskManager.printTasks();

        System.out.println("\n\rтренировку с тасками закончили");

        System.out.println("-".repeat(46)+"\n\r\n\rтрениреуемся с эпиками и подзадачами:\n\r1.создать несколько новых");
        int epic1Id = taskManager.getId();
        taskManager.addEpic(new Epic("epic1", "купить продуктов", epic1Id, null));
        int subtask11Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask1_1", "купить хлеба", subtask11Id, epic1Id, TaskState.NEW));
        int subtask12Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask1_2", "купить молока", subtask12Id, epic1Id, TaskState.NEW));
        int subtask13Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask1_3", "купить картошки", subtask13Id, epic1Id, TaskState.NEW));
        int subtask14Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask1_4", "купить чай", subtask14Id, epic1Id, TaskState.NEW));

        int epic2Id = taskManager.getId();
        taskManager.addEpic(new Epic("epic2", "делать уроки с детьми", epic2Id, null));
        int subtask21Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask2_1", "делать алгебру", subtask21Id, epic2Id, TaskState.NEW));
        int subtask22Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask2_2", "делать геометрию", subtask22Id, epic2Id, TaskState.NEW));
        int subtask23Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask2_3", "делать физику", subtask23Id, epic2Id, TaskState.NEW));
        int subtask24Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask2_4", "делать информатику", subtask24Id, epic2Id, TaskState.NEW));
        int subtask25Id = taskManager.getId();
        taskManager.addSubtask(new Subtask("subtask2_5", "делать английский", subtask25Id, epic2Id, TaskState.NEW));

        int epic3Id = taskManager.getId();
        taskManager.addEpic(new Epic("epic3", "поехать на дачу", epic3Id, null));
        taskManager.addSubtask(new Subtask("subtask3_1", "тошнить в пробке", taskManager.getId(), epic3Id, TaskState.NEW));
        taskManager.addSubtask(new Subtask("subtask3_2", "чистить снег", taskManager.getId(), epic3Id, TaskState.NEW));
        taskManager.addSubtask(new Subtask("subtask3_3", "топить печку", taskManager.getId(), epic3Id, TaskState.NEW));

        taskManager.printAll();

        System.out.println("\n\r2. меняем статус первой подзадаче первого эпика");
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask11Id).getName(),
                taskManager.getSubtaskList().get(subtask11Id).getDescription(),
                taskManager.getSubtaskList().get(subtask11Id).getId(),
                taskManager.getSubtaskList().get(subtask11Id).getEpicId(),
                TaskState.IN_PROGRESS)
        );
        System.out.println("2.1 смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.2 завершаем все подзадачи первого эпика");
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask11Id).getName(),
                taskManager.getSubtaskList().get(subtask11Id).getDescription(),
                taskManager.getSubtaskList().get(subtask11Id).getId(),
                taskManager.getSubtaskList().get(subtask11Id).getEpicId(),
                TaskState.DONE)
        );
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask12Id).getName(),
                taskManager.getSubtaskList().get(subtask12Id).getDescription(),
                taskManager.getSubtaskList().get(subtask12Id).getId(),
                taskManager.getSubtaskList().get(subtask12Id).getEpicId(),
                TaskState.DONE)
        );
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask13Id).getName(),
                taskManager.getSubtaskList().get(subtask13Id).getDescription(),
                taskManager.getSubtaskList().get(subtask13Id).getId(),
                taskManager.getSubtaskList().get(subtask13Id).getEpicId(),
                TaskState.DONE)
        );
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask14Id).getName(),
                taskManager.getSubtaskList().get(subtask14Id).getDescription(),
                taskManager.getSubtaskList().get(subtask14Id).getId(),
                taskManager.getSubtaskList().get(subtask14Id).getEpicId(),
                TaskState.DONE)
        );
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.3 удалим три подзадачи первого эпика");
        taskManager.deleteSubtask(subtask11Id);
        taskManager.deleteSubtask(subtask12Id);
        taskManager.deleteSubtask(subtask13Id);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.4 меняем статус оставшейся подзадаче первого эпика");
        //taskManager.setStatus(taskManager.getSubtask(subtask14Id), TaskState.IN_PROGRESS);
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask14Id).getName(),
                taskManager.getSubtaskList().get(subtask14Id).getDescription(),
                taskManager.getSubtaskList().get(subtask14Id).getId(),
                taskManager.getSubtaskList().get(subtask14Id).getEpicId(),
                TaskState.IN_PROGRESS)
        );
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.5 удаляем последнюю подзадачу в первом эпике");
        taskManager.deleteSubtask(subtask14Id);
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic1Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.6 стартуем первую подзадачу второго эпика");
        taskManager.updateSubtask(new Subtask(
                taskManager.getSubtaskList().get(subtask21Id).getName(),
                taskManager.getSubtaskList().get(subtask21Id).getDescription(),
                taskManager.getSubtaskList().get(subtask21Id).getId(),
                taskManager.getSubtaskList().get(subtask21Id).getEpicId(),
                TaskState.IN_PROGRESS)
        );
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic2Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.7 удаляем все подзадачи второго эпика");
        taskManager.getEpic(epic2Id).clearAllSubtasks(taskManager.getSubtaskList());
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic2Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.8 изменяем описание второго эпика");
        taskManager.updateEpic(new Epic(
                taskManager.getEpic(epic2Id).getName(),
                "доделать сегодня вечером все уроки со всеми детьми",
                taskManager.getEpic(epic2Id).getId(),
                null
                )
        );
        System.out.println("смотрим что получилось");
        taskManager.getEpic(epic2Id).printEpic(taskManager.getSubtaskList());

        System.out.println("\n\r2.9 удаляем второй эпик");
        taskManager.deleteEpic(epic2Id);
        taskManager.printEpics(true);

        System.out.println("\n\r2.10 еще раз удаляем второй эпик");
        taskManager.deleteEpic(epic2Id);


        System.out.println("\n\r2.11 удаляем все подзадачи всех эпиков");
        taskManager.deleteAllSubtasks();
        System.out.println("смотрим что получилось");
        taskManager.printAll();

        System.out.println("\n\r2.12 удаляем все эпики");
        taskManager.deleteAllEpics();
        taskManager.printAll();

        System.out.println("-".repeat(46)+"\n\rТестовый прогон программы завершен. Смотрим, что получилось");
        taskManager.printAll();

        System.out.println("\n\rВсе!\n\r" + "-".repeat(46));
    }
}
