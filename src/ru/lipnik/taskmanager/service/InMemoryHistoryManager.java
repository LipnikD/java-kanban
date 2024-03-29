package ru.lipnik.taskmanager.service;

import ru.lipnik.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task data;
        Node next;
        Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private final HashMap<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    public void linkLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        history.put(element.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
        } else if (node.next != null) {
            node.next.prev = null;
            head = node.next;
        }
        history.remove(node.data.getId());
        if (history.isEmpty()) {
            head = null;
            tail = null;
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            Node node = history.get(task.getId());
            if (node != null) {
                removeNode(node);
            }
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
        }
    }
}
