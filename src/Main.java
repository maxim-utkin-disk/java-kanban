import controllers.TaskManager;
import http.HttpTaskServer;
import utils.CheckTaskManager;
import utils.Managers;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        // строки ниже были нужны для тестирования заданий предыдущих спринтов. Могут пригодиться, оставлю.
        //CheckTaskManager testTaskMgr = new CheckTaskManager();
        //testTaskMgr.startCheckingDoublyLinkedList();
        //testTaskMgr.startCheckingFileManager();


        TaskManager taskMgr = Managers.getDefaultManager();
        CheckTaskManager.fillTaskManager(taskMgr);
        HttpTaskServer hts = new HttpTaskServer(taskMgr);
        hts.HttpTaskServerStart();
    }

}
