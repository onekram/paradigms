package expression.generic.operations;

import expression.generic.type.Mode;

@FunctionalInterface
public interface Expression<T extends Number> {
    T evaluate(Mode<T> mode, T v1, T v2, T v3);
}
