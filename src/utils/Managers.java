package utils;

import controllers.*;

import java.io.File;

import static utils.GlobalSettings.FILE_NAME;

public class Managers {

    private Managers() {
        }
        //чтобы не создавали экземпляр этого класса, использовать только статич.методы

    public static TaskManager getDefaultManager() {
        return new InMemoryTaskManager();
        //return FileBackedTaskManager.loadFromFile(new File(FILE_NAME));

    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
