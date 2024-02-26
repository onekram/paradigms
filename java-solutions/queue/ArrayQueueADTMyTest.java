package queue;

public class ArrayQueueADTMyTest {
    public static void main(String[] args) {
        ArrayQueueADT queue = ArrayQueueADT.create();
        fill(queue);
        dump(queue);
    }

    public static void fill(ArrayQueueADT queue) {
        for (int i = 0; i < 300; ++i) {
            ArrayQueueADT.enqueue(queue, "element" + i);
        }
    }
    public static void dump(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(
                    ArrayQueueADT.size(queue) + " " +
                    ArrayQueueADT.peek(queue) + " " +
                    ArrayQueueADT.dequeue(queue));
        }
    }
}
