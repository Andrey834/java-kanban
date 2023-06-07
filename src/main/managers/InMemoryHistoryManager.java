package main.managers;

import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements main.managers.HistoryManager {
    private static Node<Task> head;
    private static Node<Task> tail;
    private final static HashMap<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.get(id);
        if (node == null) {
            return;
        }

        final Node<Task> next = node.next;
        final Node<Task> previous = node.previous;

        if (Objects.equals(head, node) && Objects.equals(tail, node)) {
            head = null;
            tail = null;
        } else if (!Objects.equals(head, node) && Objects.equals(tail, node)) {
            tail = previous;
            tail.next = null;
        } else if (Objects.equals(head, node) && !Objects.equals(tail, node)) {
            head = next;
            head.previous = null;
        } else {
            previous.next = next;
            next.previous = previous;
        }
    }

    @Override
    public void clear() {
        historyMap.clear();
        head = null;
        tail = null;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> historyTasks = new ArrayList<>(historyMap.size());
        Node<Task> selectNode = head;
        while (selectNode != null) {
            historyTasks.add(selectNode.task);
            selectNode = selectNode.next;
        }
        return historyTasks;
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        historyMap.put(task.getId(), newNode);

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    public void updateNode(Task task) {
        if (historyMap.get(task.getId()) != null) {
            Node<Task> oldNode = historyMap.get(task.getId());
            oldNode.setTask(task);
            historyMap.put(task.getId(), oldNode);
        }
    }

    public static Node<Task> getHead() {
        return head;
    }

    public static Node<Task> getTail() {
        return tail;
    }

    protected static class Node<Task> {
        public Node<Task> next;
        public Task task;
        public Node<Task> previous;

        public Node(Node<Task> previous, Task task, Node<Task> next) {
            this.previous = previous;
            this.task = task;
            this.next = next;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }
}

