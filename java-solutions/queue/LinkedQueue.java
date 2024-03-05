package queue;


// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]

import java.util.Iterator;

    public class LinkedQueue extends AbstractQueue {
    private Node head;
    private Node tail;

    // Pre: true
    // Post: R.n = 0
    public LinkedQueue() {
        head = null;
        tail = null;
    }

    @Override
    protected void enqueueImpl(Object element) {
        Node node = new Node(element, null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    @Override
    public Object elementImpl() {
        return head.value;
    }

    @Override
    public Object dequeueImpl() {
        Object value = head.value;
        head = head.next;

        return value;
    }
    @Override
    public void clearImpl() {
        head = null;
        tail = null;
    }

    @Override
    protected Queue getInstance() {
        return new LinkedQueue();
    }

    private static class Node {
        private Node next;
        private final Object value;
        private Node(Object value, Node next) {
            this.next = next;
            this.value = value;
        }
    }

    public Iterator<Object> iterator() {
        return new Itr();
    }


    private class Itr implements Iterator<Object> {

        private Node currentNode;
        Itr() {
            currentNode = head;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Object next() {
            Object value = currentNode.value;
            currentNode = currentNode.next;
            return value;
        }
    }
}
