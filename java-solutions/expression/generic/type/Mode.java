package expression.generic.type;

public interface Mode<T extends Number> {
    T getFromInt(int value);
    T multiply (T v1, T v2);
    T divide (T v1, T v2);
    T add (T v1, T v2);
    T subtract(T v1, T v2);
    T negate (T v1);
}