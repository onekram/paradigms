package queue;

public class ArrayQueueModuleMyTest {
    public static void main(String[] args) {
        fill();
        dump();
    }

    public static void fill() {
        for (int i = 0; i < 4; ++i) {
            ArrayQueueModule.enqueue("element" + i);
        }
        for (int i = 0; i < 4; ++i) {
            ArrayQueueModule.push("element" + i);
        }
    }
    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(
                    ArrayQueueModule.size() + " " +
                    ArrayQueueModule.peek() + " " +
                    ArrayQueueModule.dequeue()
            );
        }
    }
}
