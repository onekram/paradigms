package expression;

import java.math.BigInteger;

public class AShift extends BinaryOperation {
    public AShift(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL >>> valueR;
    }

    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
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
    public Priority getPriority() {
        return Priority.LOW;
    }
}
