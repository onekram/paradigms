package expression;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class Const implements MyExpression {

    private final Number value;
    public Const(int value) {
        this.value = BigInteger.valueOf(value);
    }

    public Const(BigInteger value) {
        this.value = value;
    }
    @Override
    public int evaluate(int value) {
        return this.value.intValue();
    }

    @Override
    public int evaluate(int value1, int value2, int value3) {
        return this.value.intValue();
    }

    @Override
    public int evaluate(List<Integer> values) {
        return value.intValue();
    }

    public BigInteger evaluate(BigInteger value) {
        return (BigInteger) this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Const other) {
            return Objects.equals(value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
    @Override
    public String toMiniString() {
        return this.toString();
    }

    @Override
    public Priority getPriority() {
        return Priority.NP;
    }
}
