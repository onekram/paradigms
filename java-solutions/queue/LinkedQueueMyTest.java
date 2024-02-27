package queue;

public class LinkedQueueMyTest {
    public static void main(String[] args) {
        Queue queue = new LinkedQueue();
        fill(queue);
        dump(queue);
        fill(queue);
        dump(queue);
    }

    public static void fill(Queue queue) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue("el" + i);
        }
    }

    public static void dump(Queue queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    queue.size() + " " +
                            queue.dequeue());
        }
    }
}
