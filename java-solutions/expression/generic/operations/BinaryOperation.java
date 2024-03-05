package expression.generic.operations;

import expression.Priority;
import expression.generic.type.Mode;

public class BinaryOperation<T extends Number> {
    private final FuncBi<T> func;
    private final Priority priority;

    public BinaryOperation(FuncBi<T> func, Priority priority) {
        this.func = func;
        this.priority = priority;
    }

    public Expression<T> apply(final Expression<T> a, final Expression<T> b) {
        return (mode, x, y, z) -> func.evaluateImpl(mode, a.evaluate(mode, x, y, z), b.evaluate(mode, x, y, z));
    }

    public Priority getPriority() {
        return priority;
    }

    @FunctionalInterface
    public interface FuncBi<T extends Number> {
        T evaluateImpl(final Mode<T> mode, final T a, final T b);
    }
}
