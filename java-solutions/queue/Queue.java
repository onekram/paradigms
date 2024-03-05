package queue;

import java.util.Iterator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

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


    // :NOTE: v -> [null]
    // Pre: function != null
    // function: Object -> List<Object>;
    // Let: ∀ i: 1 < i < n: function(a[i]) = Fi[1..Ki]
    // ∀ el: 1 < el < Ki: Fi[el] != null
    // Post: R = r[1..∑(i = 1 to n)Ki]
    // :NOTE: p?
    // for p = 1..n: ∀ index: ∑(i = 1 to p-1)Ki < index <= ∑(i = 1 to p)Ki (K0 = 0):
    // r[index] = Fp[index - ∑(i = 1 to p-1)Ki]
    Queue flatMap(Function<Object, List<Object>> function);

    // :NOTE: init != null
    // Pre: op != null
    // Post: R = Jn,
    // Let: J1 = op(init, a[1])
    //      Jn = op(J{n-1}, a[n]), n > 1
    Object reduce(Object init, BinaryOperator<Object> op);

    // :NOTE: contract
    // Pre: true
    // Post: R = I, I.index = 0
    Iterator<Object> iterator();
}
