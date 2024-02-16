package expression;

import java.math.BigInteger;

public class Subtract extends BinaryOperation {

    public Subtract(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL - valueR;
    }

    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
        return valueL.subtract(valueR);
    }

    @Override
    protected String getSign() {
        return "-";
    }
    @Override
    public Priority getPriority() {
        return Priority.COMMON;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return false;
    }
}
