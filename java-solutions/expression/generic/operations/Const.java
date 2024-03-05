package expression.generic.operations;

import expression.generic.type.Mode;

public class Const<T extends Number> implements Expression<T> {
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public T evaluate(Mode<T> mode, T v1, T v2, T v3) {
        return mode.getFromInt(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
