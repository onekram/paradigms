package expression;

public enum Priority {
    NP(0),
    HIGH(1),
    COMMON(2),
    AND(3),
    XOR(4),
    OR(5),
    LOW(6);

    Priority(int priority) {
    }

    public static Priority getPriority(int priority) {
        return switch (priority) {
            case 1 -> HIGH;
            case 2 -> COMMON;
            case 3 -> AND;
            case 4 -> XOR;
            case 5 -> OR;
            case 6 -> LOW;
            default -> throw new AssertionError("Unexpected value: " + priority);
        };
    }
}
