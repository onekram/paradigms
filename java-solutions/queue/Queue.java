package queue;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]
public interface Queue {

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    boolean isEmpty();

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    int size();

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    void enqueue(Object element);

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable_post(n')
    Object dequeue();

    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n)
    Object element();

    // Pre: true
    // Post: n' = 0
    void clear();
}