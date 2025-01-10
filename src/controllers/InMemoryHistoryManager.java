package controllers;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private final int MAX_HISTORY_ARRAY_SIZE = 10;
    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }


    @Override
    public void add(Task task){
        if (task == null) {
            return; // пустой объект в историю просмотров не добавляем
        }
        if (history.size() == MAX_HISTORY_ARRAY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory(){
        //возвращаем копию истории
        return new ArrayList<>(history);
    }

}
