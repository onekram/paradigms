package expression;

import java.math.BigInteger;

public class RShift extends BinaryOperation {
    public RShift(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL >> valueR;
    }

    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
        return valueL.shiftRight(valueR.intValue());
    }

    @Override
    protected String getSign() {
        return ">>";
    }

    @Override
    protected boolean canOpen(MyExpression expression) {
        return false;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
}
