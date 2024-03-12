package queue;


import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]
public class ArrayQueueADT {
    private Object[] elements = new Object[2];
    private int head;

    private int size;

    // Pre: true
    // Post: R.n = 0
    public static ArrayQueueADT create() {
        return new ArrayQueueADT();
    }

    // Pre: queue != null && element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        assert queue != null;

        ensureCapacity(queue);

        int currentTail = getTail(queue);

        queue.elements[currentTail] = element;
        queue.size++;
    }

    // Pre: queue != null
    // Post: n' = n, immutable(n)
    private static void ensureCapacity(ArrayQueueADT queue) {
        assert queue != null;

        if (queue.size == queue.elements.length) {
            int currentTail = getTail(queue);
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

    // Pre: element != null && queue != null
    // Post: n' = n + 1 &&
    //       a'[1] = element &&
    //       immutable_pre(n)
    public static void push(ArrayQueueADT queue, Object element) {
        assert queue != null;

        ensureCapacity(queue);
        queue.head = cycleDec(queue, queue.head);
        queue.elements[queue.head] = element;
        queue.size++;
    }

    // Pre: n > 0 && queue != null
    // Post: R = a[1], n' = n && immutable(n')
    public static Object peek(ArrayQueueADT queue) {
        assert queue != null;
        assert !isEmpty(queue);

        return queue.elements[getPreTail(queue)];
    }

    // Pre: n > 0 && queue != null
    // Post: R = a[n] && n' = n - 1 && immutable(n')
    public static Object remove(ArrayQueueADT queue) {
        assert queue != null;
        assert !isEmpty(queue);

        int currentTail = getPreTail(queue);
        Object element = queue.elements[currentTail];
        queue.elements[currentTail] = null;
        queue.size--;
        return element;
    }

    // :NOTE: condition == null
    // Pre: queue != null && condition != null
    // el = min(A) then ∀x in A x >= el
    // Post: R: R = i: i = min({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public static int indexIf(ArrayQueueADT queue, Predicate<Object> condition) {
        assert queue != null;
        assert condition != null;

        return findIndex(queue, condition, true);
    }

    // Pre: queue != null && condition != null
    // el = max(A) then ∀x in A x <= el
    // Post: R: R = i: i = max({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public static int lastIndexIf(ArrayQueueADT queue, Predicate<Object> condition) {
        assert queue != null;
        assert condition != null;

        return findIndex(queue, condition, false);
    }
    private static int findIndex(ArrayQueueADT queue, Predicate<Object> condition, boolean firstIndex) {
        UnaryOperator<Integer> operator = el -> cycleDec(queue, el);
        int i = getPreTail(queue);
        if (firstIndex) {
            operator = el -> cycleInc(queue, el);
            i = queue.head;
        }
        for (int j = 0; j < queue.size; i = operator.apply(i), j++) {
            if (condition.test(queue.elements[i])) {
                return firstIndex ?  j : queue.size - j - 1;
            }
        }
        return -1;
    }


    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n')
    public static Object element(ArrayQueueADT queue) {
        assert queue != null;
        assert !isEmpty(queue);

        return queue.elements[queue.head];
    }

    // Pre: n > 0 && queue != null
    // Post: R = a[1] && n' = n - 1 && immutable_post(n')
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue != null;
        assert !isEmpty(queue);

        Object element = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = cycleInc(queue, queue.head);
        queue.size--;
        return element;
    }

    // Pre: true && queue != null
    // Post: R = (n = 0) && n' = n && immutable(n)
    public static boolean isEmpty(ArrayQueueADT queue) {
        assert queue != null;

        return queue.size == 0;
    }

    // Pre: queue != null
    // Post: R = n && n' = n && immutable(n)
    public static int size(ArrayQueueADT queue) {
        assert queue != null;

        return queue.size;
    }

    // Pre: queue != null
    // Post: n' = 0
    public static void clear(ArrayQueueADT queue) {
        assert queue != null;

        Arrays.fill(queue.elements, null);
        queue.head = 0;
        queue.size = 0;
    }

    // Pre: queue != null
    // Post: true
    private static int getTail(ArrayQueueADT queue) {
        assert queue != null;

        return (queue.head + queue.size) % queue.elements.length;
    }

    // Pre: queue != null
    // Post: true
    private static int getPreTail(ArrayQueueADT queue) {
        return (queue.head + queue.size - 1) % queue.elements.length;
    }

    // Pre: queue != null
    // Post: true
    private static int cycleInc(ArrayQueueADT queue, int value) {
        if (value + 1 >= queue.elements.length) {
            return value + 1 - queue.elements.length;
        }
        return value + 1;
    }

    // Pre: queue != null
    // Post: true
    private static int cycleDec(ArrayQueueADT queue, int value) {
        if (queue.elements.length + value - 1 >= queue.elements.length) {
            return queue.elements.length + value - 1 - queue.elements.length;
        }
        return queue.elements.length + value - 1;
    }
}
