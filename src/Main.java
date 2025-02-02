import utils.CheckTaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        CheckTaskManager testTaskMgr = new CheckTaskManager();
        testTaskMgr.startChecking();
    }

}
