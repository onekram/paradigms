package expression;

import java.math.BigInteger;

public class Max extends BinaryOperation {

    public Max(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return Math.max(valueL, valueR);
    }
    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
        return valueL.max(valueR);
    }

    @Override
    protected String getSign() {
        return "max";
    }
    @Override
    public int getPriority() {
        return 5;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return !(expression instanceof Min);
    }
}
