package expression;


import java.math.BigInteger;
import java.util.List;

public abstract class BinaryOperation implements MyExpression {
    private final MyExpression expressionR;
    private final MyExpression expressionL;
    public BinaryOperation(MyExpression expressionL, MyExpression expressionR) {
        this.expressionL = expressionL;
        this.expressionR = expressionR;
    }
    protected abstract int evaluateOperation(int valueL, int valueR);
    protected abstract BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR);

    @Override
    public int evaluate(int value) {
        return evaluateOperation(expressionL.evaluate(value), expressionR.evaluate(value));
    }
    @Override
    public int evaluate(int value1, int value2, int value3) {
        return evaluateOperation(expressionL.evaluate(value1, value2, value3),
                expressionR.evaluate(value1, value2, value3));

    }
    @Override
    public int evaluate(List<Integer> values) {
        return evaluateOperation(expressionL.evaluate(values),
                expressionR.evaluate(values));

    }
    @Override
    public BigInteger evaluate(BigInteger value) {
        return evaluateOperation(expressionL.evaluate(value), expressionR.evaluate(value));
    }
    protected abstract String getSign();
    @Override
    public String toString() {
         return String.format("(%s %s %s)", expressionL.toString(), getSign(), expressionR.toString());
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final var bo = (BinaryOperation) obj;
        return expressionL.equals(bo.expressionL) && expressionR.equals(bo.expressionR);
    }

    @Override
    public int hashCode() {
        return expressionL.hashCode() * 5 + expressionR.hashCode() * 7 + getSign().hashCode() * 11;
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();
        return sb.append(formatExpression(expressionL, needFormat(expressionL, false))).append(' ').
                append(getSign()).append(' ').
                append(formatExpression(expressionR, needFormat(expressionR, true))).toString();
    }

    // :NOTE: square

    private static StringBuilder formatExpression(MyExpression expression, boolean needBrackets) {
        String minString = expression.toMiniString();
        StringBuilder sb = new StringBuilder();
        if (needBrackets) {
            return sb.append('(').append(minString).append(')');
        }
        return new StringBuilder(minString);
    }

    private boolean needFormat(MyExpression expression, boolean isRight) {
        int dif = getPriority().compareTo(expression.getPriority());
        return dif < 0 || isRight && dif == 0 && !canOpen(expression);
    }

    protected abstract boolean canOpen(MyExpression expression);
}
