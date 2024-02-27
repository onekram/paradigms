package queue;


import java.util.Arrays;
import java.util.function.Predicate;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]

public class ArrayQueueModule {
    private static Object[] elements;
    private static int head;
    private static int size;

    static {
        elements = new Object[2];
        head = 0;
        size = 0;
    }

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public static void enqueue(Object element) {
        assert element != null;

        ensureCapacity();
        int currentTail = (head + size) % elements.length;

        elements[currentTail] = element;
        size++;
    }

    // Pre: true
    // Post: n' = n, immutable(n)
    private static void ensureCapacity() {
        if (size == elements.length) {
            int currentTail = (head + size) % elements.length;
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
    public static void push(Object element) {
        assert element != null;

        ensureCapacity();
        head = (elements.length + head - 1) % elements.length;
        elements[head] = element;
        size++;
    }

    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n')
    public static Object peek() {
        assert !isEmpty();
        return elements[(size + head - 1) % elements.length];
    }

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable(n')
    public static Object remove() {
        assert !isEmpty();

        int currentTail = (head + size - 1) % elements.length;
        Object element = elements[currentTail];
        elements[currentTail] = null;
        size--;
        return element;
    }

    // Pre: true
    // el = min(A) then ∀x in A x >= el
    // Post: R: R = i: i = min({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public static int indexIf(Predicate<Object> condition) {
        for (int i = head, j = 0; j < size; i = (i + 1) % elements.length, j++) {
            if (condition.test(elements[i])) {
                return j;
            }
        }
        return -1;
    }

    // Pre: true
    // el = max(A) then ∀x in A x <= el
    // Post: R: R = i: i = max({el: condition(a[el]) == true}) if exists i: condition(a[i]) == true, R = -1 otherwise
    public static int lastIndexIf(Predicate<Object> condition) {
        int lastIndex = -1;
        for (int i = head, j = 0; j < size; i = (i + 1) % elements.length, j++) {
            if (condition.test(elements[i])) {
                lastIndex = j;
            }
        }
        return lastIndex;
    }

    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n')
    public static Object element() {
        assert !isEmpty();

        return elements[head];
    }

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable_post(n')
    public static Object dequeue() {
        assert !isEmpty();

        Object element = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return element;
    }

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    public static boolean isEmpty() {
        return size == 0;
    }


    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    public static int size() {
        return size;
    }


    // Pre: true
    // Post: n' = 0
    public static void clear() {
        Arrays.fill(elements, null);
        head = 0;
        size = 0;
    }
}
