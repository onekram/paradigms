package queue;


import java.util.Iterator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

// Model: a[1..n]
// Inv: n >= 0 && for i=1..n: a[i] != null
// Let: immutable(k): for i=1..k: a'[i] = a[i]
// Let: immutable_pre(k): for i=1..k: a'[i + 1] = a[i]
// Let: immutable_post(k): for i=1..k: a'[i] = a[i + 1]
public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    public final boolean isEmpty() {
        return size == 0;
    }

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    public final int size() {
        return size;
    }

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public void enqueue(Object element) {
        assert element != null;

        enqueueImpl(element);
        size++;
    }

    // Pre: true
    // Post: n = n' && a'[n'] = element && immutable(n)
    protected abstract void enqueueImpl(Object element);


    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable_post(n')
    public Object dequeue() {
        assert !isEmpty();

        size--;
        return dequeueImpl();
    }

    // Pre: true
    // Post: R = a'[n'] && n = n' && immutable(n)
    protected abstract Object dequeueImpl();


    // Pre: n > 0
    // Post: R = a[1], n' = n && immutable(n)
    public Object element() {
        assert !isEmpty();

        return elementImpl();
    }

    // Pre: true
    // Post: R = a[1], n' = n && immutable(n)
    protected abstract Object elementImpl();

    // Pre: true
    // Post: n' = 0
    public void clear() {
        clearImpl();
        size = 0;
    }

    // Pre: true
    // Post: n = n'
    protected abstract void clearImpl();

    public abstract Iterator<Object> iterator();

    // Pre: true
    // Post: R R.n = 0
    protected abstract Queue getInstance();

    @Override
    public Queue flatMap(Function<Object, List<Object>> function) {
        assert function != null;

        Iterator<Object> it = iterator();
        Queue queue = getInstance();
        while (it.hasNext()) {
            List<Object> list = function.apply(it.next());
            for (Object element : list) {
                assert element != null;
                queue.enqueue(element);
            }
        }
        return queue;
    }

    @Override
    public Object reduce(Object init, BinaryOperator<Object> op) {
        assert op != null;

        Iterator<Object> it = iterator();
        while (it.hasNext()) {
            init = op.apply(init, it.next());
        }
        return init;
    }
}
