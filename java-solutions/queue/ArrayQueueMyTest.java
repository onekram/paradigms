package queue;

public class ArrayQueueMyTest {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();
        fill(queue);
        dump(queue);
    }

    public static void fill(ArrayQueue queue) {
        for (int i = 0; i < 300; ++i) {
            queue.enqueue("element" + i);
        }
    }
    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.peek() + " " + queue.dequeue());
        }
    }
}
