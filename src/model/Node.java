package model;

public class Node {
    public Task task;
    public Node prev;
    public Node next;

    public Node(Task task, Node prev, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    public String toString(){
        return "Узел id = " + this.task.getId()
                + ", тип = " + this.task.getClass().getName()
                + ", наименование = " + this.task.getName();
    }

}