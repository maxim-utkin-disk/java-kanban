package utils;

import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;

public class Managers {

    private Managers() {
        }
        //чтобы не создавали экземпляр этого класса, использовать только статич.методы

    public static TaskManager getDefaultManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
