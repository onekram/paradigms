package queue;


import java.util.Arrays;
import java.util.function.Predicate;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]

public class ArrayQueue {
    private Object[] elements;
    private int head;
    private int tail;
    private int currentSize;

    // Pre: true
    // Post: R.n = 0
    public ArrayQueue() {
        elements = new Object[2];
        head = 0;
        tail = 0;
        currentSize = 0;
    }

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public void enqueue(Object element) {
        assert element != null;

        ensureCapacity();

        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        ++currentSize;
    }

    // Pre: true
    // Post: n' = n, immutable(n)
    private void ensureCapacity() {
        if (currentSize == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            if (head < tail) {
                System.arraycopy(elements, head, newElements, 0, tail - head);
            } else {
                System.arraycopy(elements, head, newElements, 0, elements.length - head);
                System.arraycopy(elements, 0, newElements, elements.length - head, tail);
            }
            tail = currentSize;
            head = 0;
            elements = newElements;
        }
    }

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[1] = element &&
    //       immutable_pre(n)
    public void push(Object element) {
        assert element != null;

        ensureCapacity();
        head = (elements.length + head - 1) % elements.length;
        elements[head] = element;
        ++currentSize;
    }

    // Pre: n > 0
    // Post: R = a[n], n' = n && immutable(n')
    public Object peek() {
        assert !isEmpty();

        return elements[(elements.length + tail - 1) % elements.length];
    }

    // Pre: n > 0
    // Post: R = a[n] && n' = n - 1 && immutable(n')
    public Object remove() {
        assert !isEmpty();

        tail = (elements.length + tail - 1) % elements.length;
        Object element = elements[tail];
        elements[tail] = null;
        --currentSize;
        return element;
    }

    // Pre: true
    // Post: R: R = i: i = min({el: condition(a[i]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public int indexIf(Predicate<Object> condition) {
        for (int i = head, j = 0; j < currentSize; i = (i + 1) % elements.length, j++) {
            if (condition.test(elements[i])) {
                return j;
            }
        }
        return -1;
    }

    // Pre: true
    // Post: R: R = i: i = max({el: condition(a[i]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public int lastIndexIf(Predicate<Object> condition) {
        int lastIndex = -1;
        for (int i = head, j = 0; j < currentSize; i = (i + 1) % elements.length, j++) {
            if (condition.test(elements[i])) {
                lastIndex = j;
            }
        }
        return lastIndex;
    }

    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n)
    public Object element() {
        assert !isEmpty();

        return elements[head];
    }

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable_post(n')
    public Object dequeue() {
        assert !isEmpty();

        Object element = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        --currentSize;
        return element;
    }

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    public boolean isEmpty() {
        return currentSize == 0;
    }

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    public int size() {
        return currentSize;
    }

    // Pre: true
    // Post: n' = 0
    public void clear() {
        Arrays.fill(elements, null);
        head = 0;
        tail = 0;
        currentSize = 0;
    }
}
