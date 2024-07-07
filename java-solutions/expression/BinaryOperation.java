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
    protected abstract int evaluateImpl(int valueL, int valueR);
    protected abstract BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR);

    @Override
    public int evaluate(int value) {
        return evaluateImpl(expressionL.evaluate(value), expressionR.evaluate(value));
    }
    @Override
    public int evaluate(int value1, int value2, int value3) {
        return evaluateImpl(expressionL.evaluate(value1, value2, value3),
                expressionR.evaluate(value1, value2, value3));

    }
    @Override
    public int evaluate(List<Integer> values) {
        return evaluateImpl(expressionL.evaluate(values),
                expressionR.evaluate(values));

    }
    @Override
    public BigInteger evaluate(BigInteger value) {
        return evaluateImpl(expressionL.evaluate(value), expressionR.evaluate(value));
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

        return String.format("%s %s %s",
                formatExpression(expressionL, needFormat(expressionL, false)),
                getSign(),
                formatExpression(expressionR, needFormat(expressionR, true)));
    }

    private static String formatExpression(MyExpression expression, boolean needBrackets) {
        if (needBrackets) {
            return String.format("(%s)", expression.toMiniString());
        }
        return expression.toMiniString();
    }

    private boolean needFormat(MyExpression expression, boolean isRight) {
        return getPriority() > expression.getPriority()
                || isRight && getPriority() == expression.getPriority() && !canOpen(expression);
    }

    protected abstract boolean canOpen(MyExpression expression);
}
