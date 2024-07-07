package expression;

import java.math.BigInteger;

public class AShift extends BinaryOperation {
    public AShift(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return valueL >>> valueR;
    }

    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
        return BigInteger.valueOf(valueL.intValue() >>> valueR.intValue());
    }

    @Override
    protected String getSign() {
        return ">>>";
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
