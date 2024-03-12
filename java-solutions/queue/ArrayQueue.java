package queue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]

public class ArrayQueue extends AbstractQueue {
    private Object[] elements;
    private int head;

    // Pre: true
    // Post: R.n = 0
    public ArrayQueue() {
        elements = new Object[2];
        head = 0;
    }

    @Override
    protected void enqueueImpl(Object element) {
        ensureCapacity();
        elements[getTail()] = element;
    }

    // Pre: true
    // Post: n' = n, immutable(n)
    private void ensureCapacity() {
        if (size == elements.length) {
            int currentTail = getTail();
            Object[] newElements = new Object[elements.length * 2];
            if (head < currentTail) {
                System.arraycopy(elements, head, newElements, 0, currentTail - head);
            } else {
                System.arraycopy(elements, head, newElements, 0, elements.length - head);
                System.arraycopy(elements, 0, newElements, elements.length - head, currentTail);
            }
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
        head = cycleDec(head);
        elements[head] = element;
        ++size;
    }

    // Pre: n > 0
    // Post: R = a[n], n' = n && immutable(n')
    public Object peek() {
        assert !isEmpty();

        return elements[getPreTail()];
    }

    // Pre: n > 0
    // Post: R = a[n] && n' = n - 1 && immutable(n')
    public Object remove() {
        assert !isEmpty();

        int index = getPreTail();
        Object element = elements[index];
        elements[index] = null;
        size--;
        return element;
    }

    // Pre: true
    // el = min(A) then ∀x in A x >= el
    // Post: R: R = i: i = min({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public int indexIf(Predicate<Object> condition) {
        Iterator<Object> it = iterator();
        int j = 0;
        while (it.hasNext()) {
            if (condition.test(it.next())) {
                return j;
            }
            j++;
        }
        return -1;
    }

    // Pre: true
    // el = max(A) then ∀x in A x <= el
    // Post: R: R = i: i = max({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public int lastIndexIf(Predicate<Object> condition) {
        Iterator<Object> it = iterator();
        int j = 0;
        int lastIndex = -1;
        while (it.hasNext()) {
            if (condition.test(it.next())) {
                lastIndex = j;
            }
            j++;
        }
        return lastIndex;
    }

    @Override
    public Object elementImpl() {
        return elements[head];
    }

    @Override
    public Object dequeueImpl() {
        Object element = elements[head];
        elements[head] = null;
        head = cycleInc(head);
        return element;
    }

    public void clearImpl() {
        Arrays.fill(elements, null);
        head = 0;
    }

    // Pre: true
    // Post: true
    private int getTail() {
        return (head + size) % elements.length;
    }

    // Pre: true
    // Post: true
    private int getPreTail() {
        return (head + size - 1) % elements.length;
    }

    // Pre: true
    // Post: true
    private int cycleInc(int value) {
        return (value + 1) % elements.length;
    }

    // Pre: true
    // Post: true
    private int cycleDec(int value) {
        return (elements.length + value - 1) % elements.length;
    }
    @Override
    public Iterator<Object> iterator() {
        return new Itr();
    }
    @Override
    protected Queue getInstance() {
        return new ArrayQueue();
    }

    private class Itr implements Iterator<Object> {
        // Model: I, I[1..n], immutable(n)
        int index;
        int step;

        Itr() {
            index = head;
            step = 0;
        }
        // Pre: true
        // Post: I.index < n;
        @Override
        public boolean hasNext() {
            return step < size;
        }

        // Pre: true
        // Post: R = a[I.index'], index' = index + 1;
        @Override
        public Object next() {
            Object value = elements[index];
            index = cycleInc(index);
            step++;
            return value;
        }

    }
}
