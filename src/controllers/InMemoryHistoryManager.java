package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        nodeMap = new HashMap<>();
        first = null;
        last = null;
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return; // пустой объект в историю просмотров не добавляем
        }
        remove(task.getId());
        linkLast(task);
        nodeMap.put(task.getId(), last);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            final Node node = nodeMap.get(id);
            removeNode(node);
            nodeMap.remove(id);
            /* @avfyodorov, перенес в Менеджер задач, в метод удаления эпика
            // при удалении из истории эпика, удалим историю его подзадач
            if (node.task instanceof Epic) {
                Epic e = (Epic)node.task;
                for (int subtaskId : e.getSubtaskIdsList()) {
                    remove(subtaskId);
                }
            }*/
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node == first) {
            if (node.next != null) {
                node.next.prev = null;
                first = node.next;
            } else {
                first = null;
                last = null;
            }
        } else {
            if (node.next != null) {
                node.next.prev = node.prev;
                node.prev.next = node.next;
            } else {
                node.prev.next = null;
                last = node.prev;
            }
        }

    }

    private void linkLast(Task task) {
        final Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        }
        if (last != null) {
            last.next = node;
        }
        last = node;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        //@avfyodorov, спасибо большое за подсказку! действительно, перемудрил.
        Node node = first;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    public static class Node {
        public Task task;
        public Node prev;
        public Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        public String toString() {
            return "Узел id = " + this.task.getId()
                    + ", тип = " + this.task.getClass().getName()
                    + ", наименование = " + this.task.getName();
        }

    }
}
