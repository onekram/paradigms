package expression;


import java.math.BigInteger;
import java.util.List;

public abstract class UnaryOperation implements MyExpression {
    private final MyExpression expression;
    public UnaryOperation(MyExpression expression) {
        this.expression = expression;
    }
    protected abstract int evaluateOperation(int value);
    abstract BigInteger evaluateOperation(BigInteger value);

    @Override
    public int evaluate(int value) {
        return evaluateOperation(expression.evaluate(value));
    }

    @Override
    public int evaluate(int value1, int value2, int value3) {
        return evaluateOperation(expression.evaluate(value1, value2, value3));

    }

    @Override
    public int evaluate(List<Integer> values) {
        return evaluateOperation(expression.evaluate(values));

    }
    @Override
    public BigInteger evaluate(BigInteger value) {
        return evaluateOperation(expression.evaluate(value));
    }
    abstract String getSign();

    @Override
    public String toString() {
        return String.format("%s(%s)", getSign(), expression.toString());
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final var bo = (UnaryOperation) obj;
        return expression.equals(bo.expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode() * 5 + getSign().hashCode() * 11;
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();
        if (expression instanceof BinaryOperation) {
            sb.append(getSign()).append('(').append(expression.toMiniString()).append(')');
            return sb.toString();
        }
        sb.append(getSign()).append(' ').append(expression.toMiniString());
        return sb.toString();
    }

    @Override
    public Priority getPriority() {
        return Priority.NP;
    }

}