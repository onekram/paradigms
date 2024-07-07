package expression;

import java.math.BigInteger;

public class Min extends BinaryOperation {

    public Min(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return Math.min(valueL, valueR);
    }

    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
        return valueL.min(valueR);
    }

    @Override
    protected String getSign() {
        return "min";
    }
    @Override
    public int getPriority() {
        return 5;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return !(expression instanceof Max);
    }
}
