package queue;


import java.util.Arrays;
import java.util.function.Predicate;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]
public class ArrayQueueADT {
    private Object[] elements = new Object[2];
    // :NOTE: 3 vars
    private int head;
    private int size;

    // Pre: true
    // Post: R.n = 0
    public static ArrayQueueADT create() {
        return new ArrayQueueADT();
    }

    // :NOTE: enqueue(null, "hello")
    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;

        ensureCapacity(queue);

        int currentTail = (queue.size + queue.head) % queue.elements.length;

        queue.elements[currentTail] = element;
        ++queue.size;
    }

    // Pre: true
    // Post: n' = n, immutable(n)
    private static void ensureCapacity(ArrayQueueADT queue) {
        if (queue.size == queue.elements.length) {
            int currentTail = (queue.size + queue.head) % queue.elements.length;
            Object[] newElements = new Object[queue.elements.length * 2];
            if (queue.head < currentTail) {
                System.arraycopy(queue.elements, queue.head, newElements, 0, currentTail - queue.head);
            } else {
                System.arraycopy(queue.elements, queue.head, newElements, 0, queue.elements.length - queue.head);
                System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.head, currentTail);
            }
            queue.head = 0;
            queue.elements = newElements;
        }
    }

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[1] = element &&
    //       immutable_pre(n)
    public static void push(ArrayQueueADT queue, Object element) {
        ensureCapacity(queue);
        queue.head = (queue.elements.length + queue.head - 1) % queue.elements.length;
        queue.elements[queue.head] = element;
        queue.size++;
    }

    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n')
    public static Object peek(ArrayQueueADT queue) {
        assert !isEmpty(queue);
        return queue.elements[(queue.head + queue.size - 1) % queue.elements.length];
    }

    // Pre: n > 0
    // Post: R = a[n] && n' = n - 1 && immutable(n')
    public static Object remove(ArrayQueueADT queue) {
        assert !isEmpty(queue);

        int currentTail = (queue.head + queue.size - 1) % queue.elements.length;
        Object element = queue.elements[currentTail];
        queue.elements[currentTail] = null;
        queue.size--;
        return element;
    }

    // Pre: true
    // :NOTE: informal
    // el = min(A) then ∀x in A x >= el
    // Post: R: R = i: i = min({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public static int indexIf(ArrayQueueADT queue, Predicate<Object> condition) {
        for (int i = queue.head, j = 0; j < queue.size; i = (i + 1) % queue.elements.length, j++) {
            if (condition.test(queue.elements[i])) {
                return j;
            }
        }
        return -1;
    }

    // Pre: true
    // el = max(A) then ∀x in A x <= el
    // Post: R: R = i: i = max({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public static int lastIndexIf(ArrayQueueADT queue, Predicate<Object> condition) {
        int lastIndex = -1;
        for (int i = queue.head, j = 0; j < queue.size; i = (i + 1) % queue.elements.length, j++) {
            if (condition.test(queue.elements[i])) {
                lastIndex = j;
            }
        }
        return lastIndex;
    }

    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n')
    public static Object element(ArrayQueueADT queue) {
        assert !isEmpty(queue);

        return queue.elements[queue.head];
    }

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable_post(n')
    public static Object dequeue(ArrayQueueADT queue) {
        assert !isEmpty(queue);

        Object element = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.size--;
        return element;
    }

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    // Pre: true
    // Post: n' = 0
    public static void clear(ArrayQueueADT queue) {
        Arrays.fill(queue.elements, null);
        queue.head = 0;
        queue.size = 0;
    }
}
