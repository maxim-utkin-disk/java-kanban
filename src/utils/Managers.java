package utils;

import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
//import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import controllers.FileBackedTaskManager;

import java.io.File;

public class Managers {

    private Managers() {
        }
        //чтобы не создавали экземпляр этого класса, использовать только статич.методы

    public static TaskManager getDefaultManager() {
        //return new InMemoryTaskManager();
        return FileBackedTaskManager.loadFromFile(new File("task_list.csv"));

    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
