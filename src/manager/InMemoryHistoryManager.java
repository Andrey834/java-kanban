package manager;

import tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final HashMap<Integer, Node<Task>> nodeHashMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeHashMap.get(id);
        if (node != null) {
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
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
    }

    private List<Task> getTasks() {
        List<Task> tasksToArray = new ArrayList<>(nodeHashMap.size());
        Node<Task> selectNode = head;
        while (selectNode != null) {
            tasksToArray.add(selectNode.task);
            selectNode = selectNode.next;
        }
        return tasksToArray;
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<Task>(oldTail, task, null);
        tail = newNode;
        nodeHashMap.put(task.getId(), newNode);

        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private static class Node<Task> {
        public Node<Task> next;
        public Task task;
        public Node<Task> previous;

        public Node(Node<Task> previous, Task task, Node<Task> next) {
            this.next = next;
            this.task = task;
            this.previous = previous;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(next, node.next) && Objects.equals(task, node.task) && Objects.equals(previous, node.previous);
        }

        @Override
        public int hashCode() {
            return Objects.hash(next, task, previous);
        }
    }
}


