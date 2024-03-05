package expression.generic.operations;

import expression.generic.type.Mode;

public class UnaryOperation<T extends Number> {
    private final FuncU<T> func;

    public UnaryOperation(final FuncU<T> func) {
        this.func = func;
    }

    public Expression<T> apply(final Expression<T> a) {
        return (mode, x, y, z) -> func.evaluateImpl(mode, a.evaluate(mode, x, y, z));
    }

    @FunctionalInterface
    public interface FuncU<T extends Number> {
        T evaluateImpl(final Mode<T> mode, final T a);
    }
}