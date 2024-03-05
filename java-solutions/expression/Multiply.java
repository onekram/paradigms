package expression;

import java.math.BigInteger;

public class Multiply extends BinaryOperation {

    public Multiply(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return valueL * valueR;
    }
    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
        return valueL.multiply(valueR);
    }

    @Override
    protected String getSign() {
        return "*";
    }
    @Override
    public Priority getPriority() {
        return Priority.HIGH;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return getClass() == expression.getClass();
    }
}
