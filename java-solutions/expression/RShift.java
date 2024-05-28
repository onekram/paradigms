package expression;

import java.math.BigInteger;

public class RShift extends BinaryOperation {
    public RShift(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return valueL >> valueR;
    }

    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
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
    public int getPriority() {
        return 1;
    }
}
