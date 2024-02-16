package expression;

import java.math.BigInteger;

public interface MyExpression extends Expression, TripleExpression, BigIntegerExpression, ListExpression{
    int evaluate(int value);
    int evaluate(int value1, int value2, int value3);

    @Override
    BigInteger evaluate(BigInteger value);

    @Override
    String toString();

    String toMiniString();
    boolean equals(Object obj);
    int hashCode();

    Priority getPriority();
}
