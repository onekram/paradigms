package expression.generic.operations;

import expression.generic.type.Mode;

public class BinaryOperation<T extends Number> {
    private final FuncBi<T> func;
    private final int priority;

    public BinaryOperation(FuncBi<T> func, int priority) {
        this.func = func;
        this.priority = priority;
    }

    public Expression<T> apply(Expression<T> a, Expression<T> b) {
        return (mode, x, y, z) -> func.evaluateImpl(mode, a.evaluate(mode, x, y, z), b.evaluate(mode, x, y, z));
    }

    public int getPriority() {
        return priority;
    }

    @FunctionalInterface
    public interface FuncBi<T extends Number> {
        T evaluateImpl(Mode<T> mode, T a, T b);
    }
}
