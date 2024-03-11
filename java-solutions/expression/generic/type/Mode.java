package expression.generic.type;

public interface Mode<T extends Number> {
    T getFromInt(int value);
    T getFromString(String value);
    T multiply (T v1, T v2);
    T divide (T v1, T v2);
    T add (T v1, T v2);
    T subtract(T v1, T v2);
    T negate (T v1);
    T min (T v1, T v2);
    T max (T v1, T v2);
    T count(T v1);
}
